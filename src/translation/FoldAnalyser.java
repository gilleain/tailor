package translation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import tops.translation.model.BackboneSegment;
import tops.translation.model.Chain;
import tops.translation.model.Protein;
import tops.translation.model.Residue;
import tops.translation.model.Sheet;
import tops.translation.model.Strand;
import tops.translation.model.Terminus;

public class FoldAnalyser {

    private HBondAnalyser hBondAnalyser;

    public FoldAnalyser() {
        this.hBondAnalyser = new HBondAnalyser();

        // set some default properties
        this.hBondAnalyser.setProperty("MAX_HO_DISTANCE", "3.5");
        this.hBondAnalyser.setProperty("MIN_NHO_ANGLE", "120");
        this.hBondAnalyser.setProperty("MIN_HOC_ANGLE", "90");
    }

    public Protein analyse(Protein protein) throws PropertyException {
        for (Chain chain : protein) {

            if (chain.isDNA()) {
                continue;
            }

            this.hBondAnalyser.analyse(chain);
            this.hBondAnalyser.resetEndpoints();
            this.findSheets(chain);
            this.assignOrientationsDependingOnArchitecture(chain);
            this.determineChiralities(chain);
        }

        return protein;
    }

    public void findSheets(Chain chain) {
        // for each strand, examine all strands in front (so we don't do the same comparison twice)
        // each examination is a simple centroid-centroid distance
        ListIterator<BackboneSegment> firstSegments = chain.backboneSegmentListIterator();


        while (firstSegments.hasNext()) {
            // get the first segment, reject if not a strand
            BackboneSegment firstSegment = firstSegments.next();
            if (!(firstSegment instanceof Strand)) {
                continue;
            }

            // get the segments after the current one
            ListIterator<BackboneSegment> secondSegments = chain.backboneSegmentListIterator(firstSegment);
            while (secondSegments.hasNext()) {
                BackboneSegment secondSegment = secondSegments.next();
                if ((secondSegment != firstSegment) && (secondSegment instanceof Strand)) {
                    // make a crude distance check
                    if (this.closeApproach(firstSegment, secondSegment)) {
                        // if this passes, make a finer bonding check
                        if (this.bonded(firstSegment, secondSegment)) {
                            this.addStrandPair(firstSegment, secondSegment, chain);
                        }
                    }
                }
            }
        }
    }

    public boolean closeApproach(BackboneSegment a, BackboneSegment b) {
        Vector3d distanceVector = new Vector3d();
        distanceVector.sub(a.getAxis().getCentroid(), b.getAxis().getCentroid());
        double length = distanceVector.length();
        Logger.getLogger("translation.FoldAnalyser").info("Distance between " + a + " and " + b + " = " + Math.rint(length));
        return length < 20.0;
    }

    public boolean bonded(BackboneSegment strand, BackboneSegment otherStrand) {
        //basically, run through the residues, checking the list of hbonds to find residues that might be in the other strand
        int numberOfHBonds = 0;
        for (Residue nextResidue : strand) {
            if (otherStrand.bondedTo(nextResidue)) {
                numberOfHBonds++;
            }
            Logger.getLogger("translation.FoldAnalyser").info("Checking bonding of " + otherStrand + " and " + nextResidue + " number of bonds = " + numberOfHBonds);
        }

        //all we want to know is, are there enough hbonds for these strands to qualify as bonded
        //WARNING! this is number of RESIDUES not number of HBONDS
        if (numberOfHBonds > 0) { //?is one residue enough?
            return true;
        } else {
            Logger.getLogger("translation.FoldAnalyser").info(strand + " not bonded to " + otherStrand);
            return false;
        }
    }

    public void addStrandPair(BackboneSegment first, BackboneSegment second, Chain chain) {
        Sheet firstSheet = chain.getSheetContaining(first);
        Sheet secondSheet = chain.getSheetContaining(second);

        if (firstSheet == null && secondSheet == null) {
            chain.createSheet(first, second);
            Logger.getLogger("translation.FoldAnalyser").info("Adding " + first + " and " + second + " to new sheet");
        } else {
            if (firstSheet == null) {
                Logger.getLogger("translation.FoldAnalyser").info("Adding " + first + " and " + second + " to " + secondSheet);
                secondSheet.addPair(first, second);
            } else if (secondSheet == null) {
                Logger.getLogger("translation.FoldAnalyser").info("Adding " + first + " and " + second + " to " + firstSheet);
                firstSheet.addPair(first, second);
            } else {
                // one possibility is that both sheets are the same, and this edge closes a barrel
                if (firstSheet == secondSheet) {
                    firstSheet.addPair(first, second);
                } else {
                    // otherwise, we have to join the sheets
                    firstSheet.addPair(first, second);
                    firstSheet.extend(secondSheet);
                    chain.removeSheet(secondSheet);
                }
            }
        }
    }


    //basically, using the least-square plane to find the best chain axis is a rubbish way to do things!
    //unfortunately, the only alternative is to have some rather ad-hoc rules based on architecture
    public void assignOrientationsDependingOnArchitecture(Chain chain) {
        //for a single sheet, get the orientation of the sheet, and use that
        int sheetCount = chain.numberOfSheets();
        if (sheetCount == 1) {
            // get the sheet, and assign the orientations internally, based on relative orientations
            Iterator<Sheet> sheets = chain.sheetIterator();
            Sheet sheet = (Sheet) sheets.next();

            //System.out.println("Assigning using a single sheet");
            sheet.assignOrientationsToStrands();

            Axis sheetAxis = sheet.getAxis();
            //System.out.println("Sheet axis = " + sheetAxis.getCentroid() + ", " + sheetAxis.getAxisVector());

            // assign the helices to orientations dependant on the sheet 
            ListIterator<BackboneSegment> segments = chain.backboneSegmentListIterator();
            while (segments.hasNext()) {
                BackboneSegment segment = (BackboneSegment) segments.next();
                if ((segment instanceof Strand) || (segment instanceof Terminus)) {
                    continue;
                } else {
                    segment.determineOrientation(sheetAxis);
                }
            }
        } else if (sheetCount > 1) {
            Iterator<Sheet> sheets = chain.sheetIterator();
            while (sheets.hasNext()) {
                Sheet sheet = (Sheet) sheets.next();
                sheet.assignOrientationsToStrands();
            }
        // no sheets, take the first helix as UP
        } else {
            ListIterator<BackboneSegment> segments = chain.backboneSegmentListIterator();
            // ignore n-terminus
            segments.next();
            BackboneSegment firstSegment = (BackboneSegment) segments.next();
            firstSegment.setOrientation("UP");
            Axis firstAxis = firstSegment.getAxis();
            while (segments.hasNext()) {
                BackboneSegment segment = (BackboneSegment) segments.next();
                if (segment instanceof Terminus) {
                    continue;
                }
                segment.determineOrientation(firstAxis);
            }
        }
    }

    public void determineChiralities(Chain chain) {
        if (chain.numberOfSheets() > 0) {
            Iterator<Sheet> sheets = chain.sheetIterator();
            while (sheets.hasNext()) {
                Sheet sheet = (Sheet) sheets.next(); 
                Iterator<BackboneSegment> sheetChainOrderIterator = sheet.chainOrderIterator();
                BackboneSegment strand = (BackboneSegment) sheetChainOrderIterator.next();
        
                Logger.getLogger("translation.FoldAnalyser").info("finding chirals in sheet of length " + sheet.size());
                while (sheetChainOrderIterator.hasNext()) {
                    BackboneSegment partner = (BackboneSegment) sheetChainOrderIterator.next();
                    if (strand == partner) {
                        Logger.getLogger("translation.FoldAnalyser").info("strand == partner : " + strand + ", " + partner);
                        continue;
                    }

                    Vector3d upVector = strand.getAxis().getAxisVector();
    
                    // if the strand and its partner are less than x segments away, and parallel                  
                    int distance = chain.numberOfSegmentsInBetween(strand, partner);
                    Logger.getLogger("translation.FoldAnalyser").info("segments in between : " + strand + ", " + partner + " == " + distance);
            
                    if ((distance > 0) && (distance < 6) && (strand.getOrientation().equals(partner.getOrientation()))) {
                        Point3d strandCentroid = strand.getAxis().getCentroid();
                        Point3d partnerCentroid = partner.getAxis().getCentroid();
        
                        // get the sses we will use for the chirality calculation
                        ListIterator<BackboneSegment> inBetweeners = chain.backboneSegmentListIterator(strand, partner);

                        // find the average center of these sses
                        List<Point3d> centroids = new ArrayList<Point3d>();
                        while (inBetweeners.hasNext()) {
                            BackboneSegment segment = (BackboneSegment) inBetweeners.next();
                            centroids.add(segment.getAxis().getCentroid());
                        }
                        Point3d averageCentroid = Geometer.averagePoints(centroids);
                        Logger.getLogger("translation.FoldAnalyser").info("InBetweener centroid = " + averageCentroid);

                        //finally, do the calculation
                        char chirality = Geometer.chirality(strandCentroid, averageCentroid, partnerCentroid, upVector);
                        //System.err.println("Chiral between  " + strand + " and " + partner + " = " + chirality);
                        chain.addChirality(strand, partner, chirality);
                    }
                    strand = partner;
                }
            }
        }
    }

    public static void main(String[] args) {
        String pdbFilename = args[0];
        String cathDomainFilename = args[1];
        String logLevelString = args[2];

        try {
            Protein protein = PDBReader.read(pdbFilename);

            Logger packageLevelLogger = Logger.getLogger("translation.FoldAnalyser");
            Level logLevel = Level.parse(logLevelString);
            packageLevelLogger.setLevel(logLevel);
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new Formatter() { public String format(LogRecord record) { return record.getMessage() + "\n"; } } );
            packageLevelLogger.addHandler(consoleHandler);
            packageLevelLogger.setUseParentHandlers(false);

            FoldAnalyser foldAnalyser = new FoldAnalyser();
            foldAnalyser.analyse(protein);

            System.out.println(protein);

            ChainDomainMap cathChainDomainMap = 
            		CATHDomainFileParser.parseUpToParticularID(cathDomainFilename, protein.getID());
            Map<String, Map<String, String>> chainDomainStringMap = 
            		protein.toTopsDomainStrings(cathChainDomainMap);

            Iterator<String> itr = chainDomainStringMap.keySet().iterator();
            while (itr.hasNext()) {
                String chainID = (String) itr.next();
                Map<String, String> domainStrings = chainDomainStringMap.get(chainID);
                for (String domainString : domainStrings.keySet()) {
                    System.out.println(protein.getID() + domainString);
                }
            }

        } catch (IOException ioe) {
            System.err.println(ioe);
        } catch (PropertyException pe) {
            System.err.println(pe);
        }
    }

}
