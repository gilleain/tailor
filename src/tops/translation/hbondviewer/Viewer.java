package tops.translation.hbondviewer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import tops.translation.model.Chain;
import tops.translation.model.Domain;
import tops.translation.model.Protein;
import translation.HBondAnalyser;
import translation.PDBReader;
import translation.PropertyException;

public class Viewer extends JFrame {
	private static final long serialVersionUID = -8935885728444318196L;

	private HBondAnalyser hBondAnalyser;

//    private DropTarget dropTarget;

    private List<HBondViewPanel> views;
//    private int indexOfCurrentView = 0;
    private HBondViewPanel currentView;

    private int viewWidth;
    private int viewHeight;

    private static int DEFAULT_WIDTH = 1000;
    private static int DEFAULT_HEIGHT = 250;

    public Viewer() {
        super("HBond Viewer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.hBondAnalyser = new HBondAnalyser();
        this.hBondAnalyser.setDefaultProperties();

        this.viewWidth = Viewer.DEFAULT_WIDTH;
        this.viewHeight = Viewer.DEFAULT_HEIGHT;

        this.currentView = new HBondViewPanel(Viewer.DEFAULT_WIDTH, Viewer.DEFAULT_HEIGHT);
        this.currentView.setSize(Viewer.DEFAULT_WIDTH, Viewer.DEFAULT_HEIGHT);
        this.currentView.setLocation(1, 1);

        this.views = new ArrayList<HBondViewPanel>();
        this.views.add(this.currentView);

        this.getContentPane().add(this.currentView);
        this.setSize(Viewer.DEFAULT_WIDTH + 2, Viewer.DEFAULT_HEIGHT + 2);

//        this.dropTarget = new DropTarget(this, new ViewerDropTargetListener());

    }

    public Viewer(String pdbFilename, String propertyFilename, int width, int height) {
        this();

        this.viewWidth = width;
        this.viewHeight = height;

        this.currentView.setSize(width, height);
        this.setSize(width + 2, height + 2);

        try {
            this.loadPropertiesFromFile(propertyFilename);
        } catch (PropertyException pe) {
            System.err.println(pe);
        }

        try {
            this.viewFile(pdbFilename);
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    public void loadPropertiesFromFile(String propertyFilename) {
        try {
            this.hBondAnalyser.loadProperties(new FileInputStream(propertyFilename));
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    public void addChainToView(Chain chain, HBondViewPanel view) {
        view.setChain(chain);
    }

    public void viewFile(String filename) throws IOException {
        Protein protein = PDBReader.read(filename);
        this.hBondAnalyser.analyse(protein);

        // reset?
        int viewIndex = 0;

        for(Chain chain : protein) {
            System.out.println(chain.toTopsString(new Domain(0)));
            if (viewIndex < this.views.size()) {
                this.addChainToView(chain, this.views.get(viewIndex));
            } else {
                HBondViewPanel view = new HBondViewPanel(this.viewWidth, this.viewHeight);
                this.addChainToView(chain, view);
                this.views.add(view);
                view.setSize(this.viewWidth, this.viewHeight);
                view.setLocation(1, 1);
                this.getContentPane().add(view);
            }

            viewIndex++;
        }
    }

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("-h")) {
            System.out.println("Usage : 'Viewer <filename> <property_file> [<width> <height>]' where '<filename>' is a PDB file");
        }

        if (args.length == 0) {
            Viewer viewer = new Viewer();
            viewer.setVisible(true);
        }

        if (args.length > 1) {
            String filename         = args[0];
            String propertyFilename = args[1];

            int width = Viewer.DEFAULT_WIDTH;
            int height = Viewer.DEFAULT_HEIGHT;

            if (args.length == 4) { 
                try {
                    width = Integer.parseInt(args[2]);
                    height = Integer.parseInt(args[3]);
                } catch (NumberFormatException nfe) {
                    System.err.println(nfe);
                }
            }

            Viewer viewer = new Viewer(filename, propertyFilename, width, height);
            viewer.setVisible(true);
        }
    }

    public class ViewerDropTargetListener implements DropTargetListener {

        public void drop(DropTargetDropEvent dtde) {
            Transferable transferable = dtde.getTransferable();
            DataFlavor[] dataFlavors = transferable.getTransferDataFlavors();

            DataFlavor tmp_flavor = null;
            boolean isURL = false;
            for (int i = 0; i < dataFlavors.length; i++) {
                DataFlavor nextFlavor = dataFlavors[i];
                System.out.println(nextFlavor.getMimeType());
                if (nextFlavor.isMimeTypeEqual("application/x-java-url")) {
                    tmp_flavor = nextFlavor;
                    isURL = true;
                    break;
                } 

                if (nextFlavor.isMimeTypeEqual("application/x-java-file-list")) {
                    tmp_flavor = nextFlavor;
                    break;
                }
            }

            if (tmp_flavor != null) {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE);

                try {
                    String filename = null;
                    if (isURL) {
                        filename = ((URL) transferable.getTransferData(tmp_flavor)).getFile();
                    } else {
                    	@SuppressWarnings("unchecked")
						List<File> data = (List<File>) transferable.getTransferData(tmp_flavor);
                        filename = ((File)data.get(0)).getAbsolutePath();
                    }

                    if (filename != null) {
                        System.out.println(filename);
                        viewFile(filename);
                    }
                } catch (UnsupportedFlavorException ufe) {
                    System.err.println(ufe);
                } catch (InvalidDnDOperationException idnd) {
                    System.err.println(idnd);
                } catch (IOException ioe) {
                    System.err.println(ioe);
                } catch (ClassCastException cce) {
                    System.err.println(cce);
                }
            } else {
                dtde.rejectDrop();
            }
        }

        public void dragExit(DropTargetEvent dte) {
            //setBackground(Color.GRAY);
        }
        
        public void dragEnter(DropTargetDragEvent dtde) {
            //setBackground(Color.GREEN);
        }

        public void dropActionChanged(DropTargetDragEvent dtde) { }

        public void dragOver(DropTargetDragEvent dtde) { }
    }

}
