package tops.translation.hbondviewer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tops.translation.model.BackboneSegment;
import tops.translation.model.Chain;
import tops.translation.model.HBond;
import tops.translation.model.Helix;
import tops.translation.model.Residue;
import tops.translation.model.Strand;

public class HBondDiagramDrawer {
    private int w;
    private int h;
    private double residueAxis;
    private double backboneSegmentUpper;
    private double backboneSegmentLower;
    private Chain chain;
    private Color backgroundColor = Color.gray;
    private Font font;
    private Map<Integer, Color> colorMap;

    public HBondDiagramDrawer(int w, int h) {
        this.w = w;
        this.h = h;
        this.residueAxis = (3 * h) / 4.0;
        this.backboneSegmentUpper = (13 * h) / 16.0;
        this.backboneSegmentLower = (14 * h) / 16.0;

        this.colorMap = new HashMap<>();
        this.colorMap.put( 2, Color.lightGray);
        this.colorMap.put( 3, Color.magenta);
        this.colorMap.put( 4, Color.red);
        this.colorMap.put( 5, Color.orange);
        this.colorMap.put(-2, Color.lightGray);
        this.colorMap.put(-3, Color.blue);
        this.colorMap.put(-4, Color.green);
        this.colorMap.put(-5, Color.white);

        this.font = Font.decode("Serif-PLAIN-12");
    }

    public HBondDiagramDrawer(Chain chain, int w, int h) {
        this(w, h);
        this.chain = chain;
    }

    public void setChain(Chain chain) {
        this.chain = chain;
    }

    public void paint(Graphics g) {
        this.paint((Graphics2D) g);
    }

    public void paint(Graphics2D g2) {
        if (this.chain == null) {
            return;
        }

        double residueSeparation = (double) this.w / (double) (this.chain.length() + 1);

        g2.setColor(this.backgroundColor);
        g2.fillRect(0, 0, this.w, this.h);

        Iterator<Residue> residueIterator = this.chain.residueIterator();
        while (residueIterator.hasNext()) {
            Residue residue = residueIterator.next();
            int residueIndex = residue.getAbsoluteNumber();
            this.drawResidue(residueSeparation, residueIndex, g2);

            Iterator<HBond> hBondIterator = residue.getHBondIterator();
            while (hBondIterator.hasNext()) {
                HBond hbond = hBondIterator.next();
                Residue partner = hbond.getPartner(residue);
                int partnerIndex = partner.getAbsoluteNumber();

                if (hbond.residueIsDonor(residue)) {
                    this.drawBond(residueSeparation, residueIndex, partnerIndex, g2);
                } else {
                    this.drawBond(residueSeparation, partnerIndex, residueIndex, g2);
                }
            }
        }

        Iterator<BackboneSegment> backboneSegmentIterator = this.chain.backboneSegmentIterator();
        while (backboneSegmentIterator.hasNext()) {
            BackboneSegment backboneSegment = (BackboneSegment) backboneSegmentIterator.next();
            if (backboneSegment.length() > 0) {
                int startIndex = backboneSegment.firstResidue().getAbsoluteNumber();
                int endIndex = backboneSegment.lastResidue().getAbsoluteNumber();
                int pdbStart = backboneSegment.firstPDB();
                int pdbEnd   = backboneSegment.lastPDB();
                String type = (backboneSegment instanceof Helix)? "Helix" 
                			: (backboneSegment instanceof Strand)? "Strand" : "Other";

                this.drawBackboneSegment(residueSeparation, startIndex, endIndex, pdbStart, pdbEnd, type, g2);
            }
        } 
        
    }

    private double residuePoint(double residueSeparation, int residueIndex) {
        return (residueIndex + 1) * residueSeparation;
    }

    private void drawResidue(double residueSeparation, int residueIndex, Graphics2D g2) {
        double centerX = this.residuePoint(residueSeparation, residueIndex);

        double x = centerX - 1.0;
        double y = this.residueAxis - 1.0;
        double w = 2.0;
        double h = 2.0;

        g2.setColor(Color.black);
        g2.draw(new Arc2D.Double(x, y, w, h, 0, 360, Arc2D.CHORD));
    }

    private void drawBond(double residueSeparation, int donorIndex, int acceptorIndex, Graphics2D g2) {

        //System.err.println("Bonding " + donorIndex + " and " + acceptorIndex);

        double donorCenterX = this.residuePoint(residueSeparation, donorIndex);
        double acceptorCenterX = this.residuePoint(residueSeparation, acceptorIndex);

        double centerSeparation = Math.abs(donorCenterX - acceptorCenterX);
        double arcHeight = centerSeparation / 2.0;

        double x = (donorCenterX < acceptorCenterX) ? donorCenterX : acceptorCenterX;
        double y = this.residueAxis - (arcHeight / 2.0);
        double w = centerSeparation;
        double h = (centerSeparation / 2.0) - 2.0;

        int endpointDistance = donorIndex - acceptorIndex;
        if (endpointDistance <= 5 && endpointDistance >= -5) {
            g2.setColor((Color) this.colorMap.get(new Integer(endpointDistance)));
        } else {
            g2.setColor(Color.yellow);
        }
        g2.draw(new Arc2D.Double(x, y, w, h, 0, 180, Arc2D.OPEN));
    }

    private void drawBackboneSegment(double residueSeparation, int startIndex, int endIndex, int pdbStart, int pdbEnd, String type, Graphics2D g2) {
        double startX = this.residuePoint(residueSeparation, startIndex);
        double endX = this.residuePoint(residueSeparation, endIndex);

        double x = startX;
        double y = this.backboneSegmentUpper;
        double w = endX - startX;
        double h = this.backboneSegmentLower - this.backboneSegmentUpper;
        
        if (type.equals("Helix")) {
            g2.setColor(Color.red);
        } else if (type.equals("Strand")) {
            g2.setColor(Color.yellow);
        } else {
            g2.setColor(Color.black);
        }

        Rectangle2D backboneSegmentBox = new Rectangle2D.Double(x, y, w, h);
        g2.draw(backboneSegmentBox);

        // now draw the label
        g2.setColor(Color.black);
        g2.setFont(this.font);
        String pdbRangeLabel = pdbStart + ":" + pdbEnd;
        //System.err.println("segment " + pdbRangeLabel);

        Rectangle2D labelBounds = this.font.getStringBounds(pdbRangeLabel, g2.getFontRenderContext());
        if (w > labelBounds.getWidth()) {
            float labelX = (float) ((x + (w / 2)) - (labelBounds.getWidth() / 2));
            float labelY = (float) ((y + (h / 2)) + (labelBounds.getHeight() / 2));
            g2.drawString(pdbRangeLabel, labelX, labelY);
        }
    }
}
