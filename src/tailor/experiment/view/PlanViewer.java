package tailor.experiment.view;

import static tailor.experiment.test.Helper.pathTo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import tailor.experiment.api.Operator;
import tailor.experiment.api.PipeableOperator;
import tailor.experiment.api.Sink;
import tailor.experiment.description.ChainDescription;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.description.atom.AtomDistanceDescription;
import tailor.experiment.operator.CombineResults;
import tailor.experiment.operator.ResultPipe;
import tailor.experiment.plan.Plan;
import tailor.experiment.plan.Planner;
import tailor.experiment.plan.Result;
import tailor.experiment.test.Helper;

/**
 * Swing viewer for a Plan. Renders each Operator as a labelled rectangle,
 * connected left-to-right by lines representing ResultPipe data flow.
 */
public class PlanViewer extends JFrame {
	
	public static void main(String[] args) {
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N");
		GroupDescription groupB = Helper.makeGroupDescription("CA");
		GroupDescription groupC = Helper.makeGroupDescription("C");
		GroupDescription groupD = Helper.makeGroupDescription("O");
		chainDescription.addGroupDescriptions(groupA, groupB, groupC, groupD);
		chainDescription.addAtomListDescriptions(
				new AtomDistanceDescription(1, pathTo(groupA, "N"), pathTo(groupB, "CA")),
				new AtomDistanceDescription(1, pathTo(groupB, "CA"), pathTo(groupC, "C")),
				new AtomDistanceDescription(1, pathTo(groupC, "C"), pathTo(groupD, "O"))
		);
		
		Plan plan = new Planner().plan(chainDescription);
		PlanViewer.show(plan);
	}

    public static void show(Plan plan) {
        SwingUtilities.invokeLater(() -> new PlanViewer(plan).setVisible(true));
    }

    public PlanViewer(Plan plan) {
        super("Plan Viewer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        PlanPanel panel = new PlanPanel(plan);
        add(new JScrollPane(panel));
        pack();
        setLocationRelativeTo(null);
    }

    private static class PlanPanel extends JPanel {

        private static final int BOX_W  = 180;
        private static final int BOX_H  = 60;
        private static final int H_GAP  = 70;
        private static final int V_GAP  = 20;
        private static final int MARGIN = 30;

        /** operator id -> operator */
        private final Map<String, Operator> idToOp = new HashMap<>();
        /** operator id -> bounding rectangle in panel coordinates */
        private final Map<String, Rectangle> positions = new LinkedHashMap<>();
        /** directed edges as {sourceId, sinkId} pairs */
        private final List<String[]> edges = new ArrayList<>();

        PlanPanel(Plan plan) {
            setBackground(Color.WHITE);

            for (Operator op : plan.getOperators()) {
                idToOp.put(op.getId(), op);
            }

            collectEdges(plan);
            assignLayers(plan);

            int maxX = positions.values().stream().mapToInt(r -> r.x + r.width).max().orElse(MARGIN);
            int maxY = positions.values().stream().mapToInt(r -> r.y + r.height).max().orElse(MARGIN);
            setPreferredSize(new Dimension(maxX + MARGIN, maxY + MARGIN));
        }

        /**
         * Walk every operator and record a directed edge wherever an outgoing
         * ResultPipe has its sink registered.
         */
        @SuppressWarnings("unchecked")
        private void collectEdges(Plan plan) {
            for (Operator op : plan.getOperators()) {
                if (op instanceof PipeableOperator pipeable) {
                	System.out.println("Found pipeable " + op.description());
                    Sink<?> sink = pipeable.getSink();
                    System.out.println("Sink " + sink);
                    if (sink instanceof ResultPipe pipe && pipe.getSinkId() != null) {
                    	System.out.println("Pipe sink id " + pipe.getSinkId());
                        edges.add(new String[]{op.getId(), pipe.getSinkId()});
                    }
                } else if (op instanceof CombineResults combine) {
                	System.out.println("Found combine " + op.description());
                    Sink<Result> output = combine.getOutput();
                    System.out.println("Sink " + output);
                    if (output instanceof ResultPipe pipe && pipe.getSinkId() != null) {
                    	System.out.println("Pipe sink id " + output.getSinkId());
                        edges.add(new String[]{op.getId(), pipe.getSinkId()});
                    }
                }
            }
        }

        /**
         * Assign each operator to a horizontal layer using longest-path from
         * the plan's start points, then translate layers to pixel positions.
         */
        private void assignLayers(Plan plan) {
            Map<String, Integer> layers = new HashMap<>();
            for (Operator start : plan.getStartPoints()) {
                layers.put(start.getId(), 0);
            }

            // Propagate: iterate until stable (handles fan-in with max-of-predecessors)
            boolean changed = true;
            while (changed) {
                changed = false;
                for (String[] edge : edges) {
                    int srcLayer = layers.getOrDefault(edge[0], 0);
                    int candidate = srcLayer + 1;
                    if (layers.getOrDefault(edge[1], -1) < candidate) {
                        layers.put(edge[1], candidate);
                        changed = true;
                    }
                }
            }

            // Operators unreachable from any start point sit at layer 0
            for (Operator op : plan.getOperators()) {
                layers.putIfAbsent(op.getId(), 0);
            }

            // Group operators by layer, preserving the order from plan.getOperators()
            Map<Integer, List<String>> byLayer = new TreeMap<>();
            for (Operator op : plan.getOperators()) {
                int layer = layers.get(op.getId());
                byLayer.computeIfAbsent(layer, k -> new ArrayList<>()).add(op.getId());
            }

            // Convert layer / row indices to pixel rectangles
            for (Map.Entry<Integer, List<String>> entry : byLayer.entrySet()) {
                int layer = entry.getKey();
                List<String> ids = entry.getValue();
                int x = MARGIN + layer * (BOX_W + H_GAP);
                for (int row = 0; row < ids.size(); row++) {
                    int y = MARGIN + row * (BOX_H + V_GAP);
                    positions.put(ids.get(row), new Rectangle(x, y, BOX_W, BOX_H));
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            drawEdges(g2);
            drawOperators(g2);
        }

        private void drawEdges(Graphics2D g2) {
            g2.setColor(new Color(80, 80, 100));
            g2.setStroke(new BasicStroke(1.5f));
            for (String[] edge : edges) {
                Rectangle src = positions.get(edge[0]);
                Rectangle dst = positions.get(edge[1]);
                if (src == null || dst == null) continue;

                int x1 = src.x + src.width;
                int y1 = src.y + src.height / 2;
                int x2 = dst.x;
                int y2 = dst.y + dst.height / 2;

                g2.drawLine(x1, y1, x2, y2);
                drawArrowHead(g2, x1, y1, x2, y2);
            }
        }

        private void drawArrowHead(Graphics2D g2, int x1, int y1, int x2, int y2) {
            double angle = Math.atan2(y2 - y1, x2 - x1);
            int   len    = 9;
            double spread = Math.PI / 7;
            int ax1 = (int)(x2 - len * Math.cos(angle - spread));
            int ay1 = (int)(y2 - len * Math.sin(angle - spread));
            int ax2 = (int)(x2 - len * Math.cos(angle + spread));
            int ay2 = (int)(y2 - len * Math.sin(angle + spread));
            g2.drawLine(x2, y2, ax1, ay1);
            g2.drawLine(x2, y2, ax2, ay2);
        }

        private void drawOperators(Graphics2D g2) {
            FontMetrics fm = g2.getFontMetrics();
            for (Map.Entry<String, Rectangle> entry : positions.entrySet()) {
                String   id   = entry.getKey();
                Rectangle rect = entry.getValue();
                Operator op   = idToOp.get(id);

                // Fill
                g2.setColor(colorFor(op));
                g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);

                // Border
                g2.setColor(colorFor(op).darker());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);

                // Two-line label: type name + "id:N"
                String typeLine = truncate(op.getClass().getSimpleName(), fm, rect.width - 10);
                String idLine   = "id:" + id;

                int lh          = fm.getHeight();
                int textBlockTop = rect.y + (rect.height - 2 * lh) / 2;
                int line1Base   = textBlockTop + fm.getAscent();
                int line2Base   = line1Base + lh;
                int cx          = rect.x + rect.width / 2;

                g2.setColor(Color.BLACK);
                g2.drawString(typeLine, cx - fm.stringWidth(typeLine) / 2, line1Base);
                g2.setColor(new Color(70, 70, 70));
                g2.drawString(idLine,   cx - fm.stringWidth(idLine)   / 2, line2Base);
            }
        }

        private static Color colorFor(Operator op) {
            if (op == null) return Color.LIGHT_GRAY;
            return switch (op.getClass().getSimpleName()) {
                case "ScanAtomResultByLabel"        -> new Color(173, 216, 230); // light blue
                case "FilterAtomResultByCondition"  -> new Color(144, 238, 144); // light green
                case "CombineResults"               -> new Color(255, 218, 185); // peach/orange
                case "PrintAdapter", "PrintResults" -> new Color(216, 191, 216); // lavender
                default                             -> new Color(220, 220, 220); // light grey
            };
        }

        private static String truncate(String s, FontMetrics fm, int maxWidth) {
            if (fm.stringWidth(s) <= maxWidth) return s;
            while (s.length() > 1 && fm.stringWidth(s + "…") > maxWidth) {
                s = s.substring(0, s.length() - 1);
            }
            return s + "…";
        }
    }
}
