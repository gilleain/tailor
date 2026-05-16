package translation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import tops.translation.model.Chain;
import tops.translation.model.Environment;
import tops.translation.model.HBond;
import tops.translation.model.Protein;
import tops.translation.model.Group;
import tops.translation.model.Segment;
import tops.translation.model.Segment.Type;
import tops.translation.model.Sheet;

public class StructureFinder {
    
    private Protein protein;
    
    private final Logger LOG = Logger.getLogger(StructureFinder.class.getName());

    public StructureFinder(String filename) throws IOException {
        this.protein = PDBReader.read(filename);
    }

    public Protein getProtein() {
        return this.protein;
    }

    // The 'interface' to the function of this class
    // I suppose that rigorously, this should be the only public method...
    public void findStructure(Chain chain) {
        chain.calculateTorsions();
        this.calculateHBondPartners(chain);
        //this.assignTorsionsAndHBondsToTypes(chain);
        //this.convertTorsionsToRepetitiveStructure(chain);
        //this.cleanStructure(chain);
        //this.cleanStructure(chain);
        this.buildSSES(chain);
        this.findSheets(chain);
        this.assignOrientationsDependingOnArchitecture(chain);
        this.determineChiralities(chain);
    }

    // go through the fixed structures, finding helices connecting parallel strands not more than 5 sses away
    public void determineChiralities(Chain chain) {
        if (chain.numberOfSheets() > 0) {
            Iterator<Sheet> sheets = chain.sheetIterator();
            while (sheets.hasNext()) {
                Sheet sheet = sheets.next();
                Iterator<Segment> sheetIterator = sheet.iterator();
                Segment strand = (Segment) sheetIterator.next();

                while (sheetIterator.hasNext()) {
                    Segment partner = (Segment) sheetIterator.next();
                    boolean isCorrectOrder = strand.compareTo(partner) < 0;
                    Vector3d upVector = strand.getAxis().getAxisVector();

                    // if the strand and its partner are less than x segments away, and parallel
                    int distance;
                    if (isCorrectOrder) {
                        distance = chain.numberOfSegmentsInBetween(strand, partner);
                    } else {
                        distance = chain.numberOfSegmentsInBetween(partner, strand);
                    }

                    if ((distance < 11) && (strand.getOrientation().equals(partner.getOrientation()))) {
                        Point3d strandCentroid = strand.getAxis().getCentroid();
                        Point3d partnerCentroid = partner.getAxis().getCentroid();

                        // get the sses we will use for the chirality calculation
                        ListIterator<Segment> inBetweeners;
                        if (isCorrectOrder) {
                            inBetweeners = chain.segmentListIterator(strand, partner);
                        } else {
                            inBetweeners = chain.segmentListIterator(partner, strand);
                        }

                        // find the average center of these sses
                        List<Point3d> centroids = new ArrayList<>();
                        while (inBetweeners.hasNext()) {
                            Segment segment = inBetweeners.next();
                            centroids.add(segment.getAxis().getCentroid()); 
                        }
                        Point3d averageCentroid = Geometer.averagePoints(centroids);
                        LOG.info("InBetweener centroid = " + averageCentroid);

                        //finally, do the calculation
                        char chirality;
                        if (isCorrectOrder) {
                            chirality = Geometer.chirality(strandCentroid, averageCentroid, partnerCentroid, upVector);
                        } else {
                            chirality = Geometer.chirality(partnerCentroid, averageCentroid, strandCentroid, upVector);
                        }
                    
                        if (isCorrectOrder) {
                            chain.addChirality(strand, partner, chirality);
                        } else {
                            chain.addChirality(partner, strand, chirality);
                        }
                    }
                    strand = partner;
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
            Sheet sheet = sheets.next();

            //System.out.println("Assigning using a single sheet");
            sheet.assignOrientationsToStrands();

            Axis sheetAxis = sheet.getAxis();
            //System.out.println("Sheet axis = " + sheetAxis.getCentroid() + ", " + sheetAxis.getAxisVector());

            // assign the helices to orientations dependant on the sheet 
            ListIterator<Segment> segments = chain.segmentListIterator();
            while (segments.hasNext()) {
                Segment segment = segments.next();
                if (segment.getType() == Type.STRAND 
                		|| segment.getType() == Type.NTERMINUS 
                		|| segment.getType() == Type.CTERMINUS) {
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
        }
    }

    public void assignTorsionsAndHBondsToTypes(Chain chain) {
        //run through once, using hbonds and torsions to attempt individual residue assignments
        Iterator<Group> residueIterator = chain.residueIterator();
        Map<Group, Character> residueAssignments = new HashMap<>();

        //temporary buffers to store char assignents
        StringBuilder torsionBuffer = new StringBuilder();
        StringBuilder hbondBuffer = new StringBuilder();
        StringBuilder assignBuffer = new StringBuilder();

        StringBuilder tmpTorsion = new StringBuilder();
        StringBuilder tmpHBond = new StringBuilder();

        int spaceCounter = 1;
        while (residueIterator.hasNext()) {
            Group residue = residueIterator.next();

            char typeFromTorsion = this.determineTypeFromTorsion(residue);
            torsionBuffer.append(typeFromTorsion);

            tmpTorsion.append(typeFromTorsion);
            if (spaceCounter % 10 == 0) { tmpTorsion.append(' '); }

            char typeFromHBonds  = this.determineTypeFromHBonds(residue);
            hbondBuffer.append(typeFromHBonds);

            tmpHBond.append(typeFromHBonds);
            if (spaceCounter % 10 == 0) { tmpHBond.append(' '); }
            
            char assignment = this.assign(typeFromTorsion, typeFromHBonds);
            assignBuffer.append(assignment);
            if (spaceCounter % 10 == 0) { assignBuffer.append(' '); }

            residueAssignments.put(residue, assignment);

            spaceCounter++;
        }

        //now, run through the assignments, merging into sses
        int sseStart = 1;
        int sseEnd = -1;
        char currentSSEType = 'U';
        List<Segment> backboneSegments = new ArrayList<>();
        for (int index = 0; index < torsionBuffer.length(); index++) {
            char sseChar = torsionBuffer.charAt(index);
            // same type : extend the end of current
            if (sseChar == currentSSEType) {
                sseEnd = index + 1;
            // change of type : finish previous and start new
            } else {
                LOG.info("SSE : " + currentSSEType + " from " + sseStart + " to " + sseEnd);
                backboneSegments.add(this.createSegment(sseStart, sseEnd, currentSSEType, chain));
                sseStart = index + 1;
                sseEnd = index + 1;
            } 
            currentSSEType = sseChar;
        }
        LOG.info("SSE : " + currentSSEType + " from " + sseStart + " to " + sseEnd);

        this.trimByHBonds(backboneSegments, hbondBuffer); 
    }
    
    public void trimByHBonds(List<Segment> backboneSegments, StringBuilder hbondBuffer) {
        ListIterator<Segment> itr = backboneSegments.listIterator();
        Segment previousSegment = null;

        while (itr.hasNext()) {
            // get the current and the next segments in the list
            Segment currentSegment = itr.next();
            Segment nextSegment = null;
            if (itr.hasNext()) {
                nextSegment = itr.next();
                itr.previous();
            }

            // helices
            if (currentSegment.isType(Type.HELIX) && currentSegment.length() < 4) {
                if (previousSegment != null && previousSegment.isType(Type.UNSTRUCTURED)) {
                    if (nextSegment != null && nextSegment.isType(Type.UNSTRUCTURED)) {
                        this.mergeThreeSegments(itr, previousSegment, currentSegment, nextSegment);            
                    } else {
                        previousSegment.mergeWith(currentSegment);
                        itr.remove();
                        currentSegment = previousSegment;
                    }
                } else {
                    if (nextSegment != null && nextSegment.isType(Type.UNSTRUCTURED)) {
                        nextSegment.mergeWith(currentSegment);
                        itr.previous();
                        itr.remove();
                        currentSegment = nextSegment;
                    } else {
                        // a short helix surrounded by strand? => merge?
                        if (nextSegment != null) {
                            this.mergeThreeSegments(itr, previousSegment, currentSegment, nextSegment);            
                        }
                    }
                }
            }

            // strands
    
            previousSegment = currentSegment;
            LOG.info("Setting previous segment to : " + previousSegment);
        }
    }

    public Segment createSegment(int start, int end, int sseType, Chain chain) {
        // make a new segment of the appropriate type
        Segment newSegment = null;
        if (sseType == 'U') {
            newSegment = new Segment(Type.UNSTRUCTURED);
        } else if (sseType == 'E') {
            newSegment = new Segment(Type.STRAND);
        } else if (sseType == 'H') {
            newSegment = new Segment(Type.HELIX);
        } else {
            newSegment = new Segment(Type.UNSTRUCTURED);
        }

        // fill the new segment with residues
        for (int index = start; index <= end; index++) {
            Group residue = chain.getResidueByAbsoluteNumbering(index);
            newSegment.expandBy(residue);
        }

        // return this new, filled segment
        return newSegment;
    }

    public char assign(char typeFromTorsion, char typeFromHBonds) {
        //firstly, if we agree, then all is fine and dandy
        if (typeFromTorsion == typeFromHBonds) {
            return typeFromTorsion;
        } else {
            //torsions may fall outside the narrow bounds defined for them
            if (typeFromTorsion == 'U') {
                return typeFromHBonds;
            } else {
                //it is possible for a residue in an SSE not to have any bonds
                if (typeFromHBonds == 'U') {
                    return typeFromTorsion;
                } else {
                    //torsion == 'H' and hbonds == 'E'
                    if (typeFromTorsion == 'H') {
                        return 'E';
                    //torsion == 'E' and hbonds == 'H' - can this happen?
                    } else {
                        return 'H';
                    }
                }
            }
        }
    }

    public char determineTypeFromTorsion(Group residue) {
    	if (torsionsMatch(Type.HELIX, residue)) {
    		return 'H';
    	} else if (torsionsMatch(Type.STRAND, residue)) {
            return 'E';
        } else {
            return 'U';
        }
    }

    public char determineTypeFromHBonds(Group residue) {
        if (hbondsMatch(Type.HELIX, residue)) {
            return 'H';
        } else if (hbondsMatch(Type.STRAND, residue)) {
            return 'E';
        } else {
            return 'U';
        }
    }
    
    public boolean torsionsMatch(Type type, Group residue) {
    	if (type == Type.HELIX) {
    		int phiMin = -110;
    	    int phiMax = -30;
    	    int psiMin = -80;
    	    int psiMax = -20;
    		return torsionsMatch(residue, phiMin, phiMax, psiMin, psiMax);
    	} else if (type == Type.STRAND) {
    		int phiMin = -170;
    	    int phiMax = -60;
    	    int psiMin = 110;
    	    int psiMax = 180;
    	    return torsionsMatch(residue, phiMin, phiMax, psiMin, psiMax);
    	} else {
    		return false;
    	}
    }
    
    public boolean torsionsMatch(Group residue, int phiMin, int phiMax, int psiMin, int psiMax) {
        double phi = residue.getPhi();
        double psi = residue.getPsi();
        boolean phiMatches = phi < phiMax && phi > phiMin;
        boolean psiMatches = psi < psiMax && psi > psiMin;
        return phiMatches && psiMatches;
    }
    
    public boolean hbondsMatch(Type type, Group r) {
    	if (type == Type.HELIX) {
    		return r.getHBonds().stream().anyMatch(HBond::hasHelixResidueSeparation);
    	} else if (type == Type.STRAND) {
    		return r.getHBonds().stream().anyMatch(HBond::hasSheetResidueSeparation);
    	} else {
    		return false;
    	}
    }

    public void findSheets(Chain chain) {
        //for each strand, examine all strands in front (so we don't do the same comparison twice)
        //each examination is a simple centroid-centroid distance
        ListIterator<Segment> firstSegments = chain.segmentListIterator();
    
        while (firstSegments.hasNext()) {
            // get the first segment, reject if not a strand
            Segment firstSegment = (Segment) firstSegments.next();
            if (!firstSegment.isType(Type.STRAND)) {
                continue;
            }
           
            // get the segments after the current one
            ListIterator<Segment> secondSegments = chain.segmentListIterator(firstSegment);
            while (secondSegments.hasNext()) {
                Segment secondSegment = secondSegments.next();
                if ((secondSegment != firstSegment && secondSegment.isType(Type.STRAND))
                    //make a crude distance check
                    && closeApproach(firstSegment, secondSegment) 
                        //if this passes, make a finer bonding check
                    && bonded(firstSegment, secondSegment)
                ) {
                            //this.addStrandPair(firstSegment, secondSegment, chain);
                }
            }
        }
    }

    /*
    public void addStrandPair(BackboneSegment first, BackboneSegment second, Chain chain) {
        Sheet firstSheet = chain.getSheetContaining(first);
        Sheet secondSheet = chain.getSheetContaining(second);
        
        if (firstSheet == null && secondSheet == null) {
            chain.createSheet(first, second);
            //System.out.println("Adding " + first + " and " + second + " to new sheet");
        } else {
            if (firstSheet == null) {
                //System.out.println("Adding " + first + " and " + second + " to " + secondSheet);
                secondSheet.insert(second, first);
            } else if (secondSheet == null) {
                //System.out.println("Adding " + first + " and " + second + " to " + firstSheet);
                firstSheet.insert(first, second);
            } else {
                // one possibility is that both sheets are the same, and this edge closes a barrel
                if (firstSheet == secondSheet) {
                    firstSheet.closeBarrel(first, second);
                } else {
                    // otherwise, we have to join the sheets
                    this.joinSheets(first, second, firstSheet, secondSheet, chain);
                }
            }
        }
    }

    public void joinSheets(BackboneSegment first, BackboneSegment second, Sheet firstSheet, Sheet secondSheet, Chain chain) {
        // check that the strands are not somehow in the middle of the sheet (bifurcated sheets)
        if (firstSheet.strandInMiddle(first) || secondSheet.strandInMiddle(second)) {
            System.err.println("bifurcation in : Sheet (" + firstSheet + ") and Strand (" + first + ") or Sheet (" + secondSheet + ") and Strand (" + second + " )");
            return;
        }

        int indexOfFirst = firstSheet.indexOf(first);
        int indexOfSecond = secondSheet.indexOf(second);

        if (indexOfSecond == 0) {
            if (indexOfFirst == 0) {
                //System.out.println("Reversing : " + firstSheet + " and adding : " + secondSheet + " onto the end");
                firstSheet.reverse();
                firstSheet.extend(secondSheet);
            } else {
                //System.out.println("Adding : " + secondSheet + " onto the end of " + firstSheet);
                firstSheet.extend(secondSheet);
            }
            chain.removeSheet(secondSheet);
        } else {
            if (indexOfFirst == 0) {
                //System.out.println("Adding : " + firstSheet + " onto the end of " + secondSheet);
                secondSheet.extend(firstSheet);
                chain.removeSheet(firstSheet);
            } else {
                //System.out.println("Reversing : " + secondSheet + " and adding it onto the end of " + firstSheet);
                secondSheet.reverse();
                firstSheet.extend(secondSheet);
                chain.removeSheet(secondSheet);
            }
        }
    }
    */

    public boolean closeApproach(Segment a, Segment b) {
        Vector3d distanceVector = new Vector3d();
        distanceVector.sub(a.getAxis().getCentroid(), b.getAxis().getCentroid());
        double length = distanceVector.length();
        //System.out.println("Distance between " + a + " and " + b + " = " + Math.rint(length));
        return length < 10.0;
    }

    public boolean bonded(Segment strand, Segment otherStrand) {
        //basically, run through the residues, checking the list of hbonds to find residues that might be in the other strand
        int numberOfHBonds = 0;
        for (Group nextResidue : strand.getResidues()) {
            if (otherStrand.bondedTo(nextResidue)) {
                numberOfHBonds++;
            }
        }

        //all we want to know is, are there enough hbonds for these strands to qualify as bonded
        //WARNING! this is number of RESIDUES not number of HBONDS
        if (numberOfHBonds > 1) { //?is two enough?
            return true;
        } else {
            LOG.info(strand + " not bonded to " + otherStrand);
            return false;
        }
    }


    //merge RepetitiveStructure separated by only a single unstructured residue
    //also, delete single-residue RepetitiveStructure surrounded by UnstructuredRegions
    public void cleanStructure(Chain chain) {
        ListIterator<Segment> backboneSegmentIterator = chain.segmentListIterator();
        Segment currentSegment = null;

        while (backboneSegmentIterator.hasNext()) {
            Segment previousSegment = currentSegment;
            currentSegment = backboneSegmentIterator.next();

            //Don't bother about the terminii
            if (currentSegment.isType(Type.NTERMINUS) || currentSegment.isType(Type.CTERMINUS)) {
                continue;
            }

            //Consider the unstructured parts in between for deletion
            if (currentSegment.isType(Type.UNSTRUCTURED)) {
                //only merge if the unstructured segment is short
                if (currentSegment.length() > 2) {
                    continue;
                }
                //check the segments 'fore and 'aft - are they continuous?
                if (backboneSegmentIterator.hasNext()) {
                    Segment nextSegment = backboneSegmentIterator.next();
                    LOG.info("Checking : " + previousSegment + " and " + currentSegment + " and " + nextSegment);
                    if (previousSegment != null && previousSegment.continuousWith(nextSegment)) {
                        this.mergeThreeSegments(backboneSegmentIterator, previousSegment, currentSegment, nextSegment);
                        continue;
                    } else {
                        backboneSegmentIterator.previous();
                    }
                }
            //Or, if it is repetitive, and surrounded by unstructured, and short, delete it.
            } else {
                //only delete if the Repetitive segment is short
                if (currentSegment.length() > 2) {
                    continue;
                }

                //check the segments 'fore and 'aft - are they unstructured?
                if (backboneSegmentIterator.hasNext()) {
                    Segment nextSegment = backboneSegmentIterator.next();
                    LOG.info("Checking : " + previousSegment + " and " + currentSegment + " and " + nextSegment);
                    if (previousSegment.isType(Type.UNSTRUCTURED) && nextSegment.isType(Type.UNSTRUCTURED)) {
                        this.mergeThreeSegments(backboneSegmentIterator, previousSegment, currentSegment, nextSegment);
                        continue;
                    } else {
                        backboneSegmentIterator.previous();
                    }
                }
                
            }
        }
    }

    public void mergeThreeSegments(ListIterator<Segment> backboneSegmentIterator, 
    							   Segment previousSegment, 
    							   Segment currentSegment, 
    							   Segment nextSegment) {
        LOG.info("Merging : " + previousSegment + " and " + currentSegment + " and " + nextSegment);
        //merge the previous segment with the current segment and the next segment
        previousSegment.mergeWith(currentSegment);
        previousSegment.mergeWith(nextSegment);
        //now, delete the current and next segments
        backboneSegmentIterator.previous();
        backboneSegmentIterator.remove();
        backboneSegmentIterator.previous();
        backboneSegmentIterator.remove();
    }

    public Segment fitNextResidue(Group residue, Segment currentBackboneSegment) {
        if (currentBackboneSegment.isType(Type.STRAND)) {
            if (torsionsMatch(Type.STRAND, residue)) {
                currentBackboneSegment.expandBy(residue);
                return currentBackboneSegment;
            } else if (torsionsMatch(Type.HELIX, residue)) {
                return new Segment(Type.HELIX, residue);
            } else {
                return new Segment(Type.UNSTRUCTURED, residue);
            }
        } else if (currentBackboneSegment.isType(Type.HELIX)) {
            if (torsionsMatch(Type.HELIX, residue)) {
                currentBackboneSegment.expandBy(residue);
                return currentBackboneSegment;
            } else if (torsionsMatch(Type.STRAND, residue)) {
                return new Segment(Type.STRAND, residue);
            } else {
            	 return new Segment(Type.UNSTRUCTURED, residue);
            }
        } else {
            if (torsionsMatch(Type.STRAND, residue)) {
                return new Segment(Type.STRAND, residue);
            } else if (torsionsMatch(Type.HELIX, residue)) {
                return new Segment(Type.HELIX, residue);
            } else {
                currentBackboneSegment.expandBy(residue);
                return currentBackboneSegment;
            }
        }
    }

    public void convertTorsionsToRepetitiveStructure(Chain chain) {
        Iterator<Group> residueIterator = chain.residueIterator();
        Segment nterminus = new Segment(Type.NTERMINUS);
        chain.addBackboneSegment(nterminus);
        Segment currentSegment = new Segment(Type.UNSTRUCTURED);

        while (residueIterator.hasNext()) {
            Group residue = residueIterator.next();
            Segment nextSegment = this.fitNextResidue(residue, currentSegment);
            if (nextSegment != currentSegment) {
                chain.addBackboneSegment(currentSegment);   //store the previous segment
            }
            currentSegment = nextSegment;
        }
        chain.addBackboneSegment(currentSegment);       //store the final segment
        chain.addBackboneSegment(new Segment(Type.CTERMINUS));    //and add a C Terminus for luck
    }

    public void calculateHBondPartners(Chain c) {
        Iterator<Group> itr = c.residueIterator();
        while (itr.hasNext()) {
            Group first = (Group) itr.next();
            this.searchForwards(first, c);
        }
    }

    public void searchForwards(Group first, Chain c) {

        // check that it makes sense to be doing this
        if (!first.isStandardAminoAcid()) {
            return;
        }

        int position = first.getAbsoluteNumber();
        int nextPosition = position + 1;
        Point3d firstN = first.getCoordinates("N");
        Point3d firstH = first.getCoordinates("H");
        Point3d firstO = first.getCoordinates("O");
        Point3d firstC = first.getCoordinates("C");

        // FIXME : unfortunately, this misses out on PRO residues (also below)
        if (firstN == null || firstH == null || firstO == null || firstC == null) {
            return;
        }

        // allow for chain breaks, or return if we have reached the end
        if (!c.hasResidueByAbsoluteNumbering(nextPosition)) {
            Group second = c.getNextResidue(position);
            if (second == null) {   //probably reached the end of the chain!
                return ;
            }
            nextPosition = second.getAbsoluteNumber();
        }

        // now, compare the first residue to the residues further on in the chain
        Iterator<Group> itr = c.residueIterator(nextPosition);
        while (itr.hasNext()) {
            int secondPosition = itr.next().getAbsoluteNumber();
            if (secondPosition < (position + 3)) {
                continue;
            }

            Group second;
            try {
                second = c.getResidueByAbsoluteNumbering(secondPosition);
                if (!second.isStandardAminoAcid()) {
                    continue;
                }
            } catch (IndexOutOfBoundsException i) {
                break;
            }

            try {
                Point3d secondN = second.getCoordinates("N");
                Point3d secondH = second.getCoordinates("H");
                Point3d secondO = second.getCoordinates("O");
                Point3d secondC = second.getCoordinates("C");

                // FIXME : PRO residues...
                if (secondN == null || secondH == null || secondO == null || secondC == null) {
                    continue;
                }

                // bonds from first N-H to second C=O
                HBond firstSecondBond = this.createBond(first, second, firstN, firstH, secondO, secondC);
                if (firstSecondBond != null) {
                    c.addHBond(firstSecondBond);
                    first.addHBond(firstSecondBond);
                    second.addHBond(firstSecondBond);
                }

                // bonds from second N-H to first C=O
                HBond secondFirstBond = this.createBond(second, first, secondN, secondH, firstO, firstC);
                if (secondFirstBond != null) {
                    c.addHBond(secondFirstBond);
                    first.addHBond(secondFirstBond);
                    second.addHBond(secondFirstBond);
                }
            } catch (NullPointerException npe) {
                LOG.severe(npe + " for " + first + " and " + second);
                continue;
            }
        }
        // finally, as an optimisation measure, use this loop over the residues to analyse the bonding environment
        this.analyzeHBonds(first);
    }

    public HBond createBond(Group donor, Group acceptor, Point3d nitrogen, Point3d hydrogen, Point3d oxygen, Point3d carbon) {
        double distance = hydrogen.distance(oxygen);
        double nhoAngle = Geometer.angle(nitrogen, hydrogen, oxygen);
        double hocAngle = Geometer.angle(hydrogen, oxygen, carbon);
        
        if (distance < 3.5 && nhoAngle > 120.0 && hocAngle > 90.0) {
            return new HBond(donor, acceptor, distance, nhoAngle, hocAngle);
        } else {
            return null;
        }
    }

    public void analyzeHBonds(Group residue) {
        List<HBond> nTerminalHBonds = residue.getNTerminalHBonds();
        List<HBond> cTerminalHBonds = residue.getCTerminalHBonds();
        int numberOfNTerminalHBonds = nTerminalHBonds.size();
        int numberOfCTerminalHBonds = cTerminalHBonds.size();

        LOG.info(numberOfNTerminalHBonds + ", " + numberOfCTerminalHBonds + " for " + residue);

        if (numberOfNTerminalHBonds == 0 && numberOfCTerminalHBonds == 0) {
            residue.setEnvironment(Environment.LOOP);
        } else if (numberOfNTerminalHBonds == 1 && numberOfCTerminalHBonds == 0) {
            if (nTerminalHBonds.get(0).hasHelixResidueSeparation()) {
                residue.setEnvironment(Environment.HELIX_END);
            }
        } else if (numberOfNTerminalHBonds == 0 && numberOfCTerminalHBonds == 1) {
            if (cTerminalHBonds.get(0).hasHelixResidueSeparation()) {
                residue.setEnvironment(Environment.HELIX_START);
            }
        } else if (numberOfNTerminalHBonds == 1 && numberOfCTerminalHBonds == 1) {
            int n = nTerminalHBonds.get(0).getResidueSeparation();
            int c = cTerminalHBonds.get(0).getResidueSeparation();
            //System.err.println(n + ", " + c + " for " + residue);

            if (n == 4 && c == 4) {
                residue.setEnvironment(Environment.HELIX_MIDDLE);
            } else if (Math.abs(n - c) == 2) {
                residue.setEnvironment(Environment.PARALLEL_STRAND);
            } else if (n - c == 0) {
                residue.setEnvironment(Environment.ANTIPARALLEL_STRAND);
            } else if (n == 4) {
                residue.setEnvironment(Environment.HELIX_END);
            } else if (c == 4) {
                residue.setEnvironment(Environment.HELIX_START);
            }
        }
    }

    public void buildSSES(Chain chain) {
        chain.addBackboneSegment(new Segment(Type.NTERMINUS));
        this.buildHelices(chain);
        this.buildStrands(chain);
        //this.buildLoops(chain);
        chain.sortSegments();
        chain.addBackboneSegment(new Segment(Type.CTERMINUS));    //and add a C Terminus for luck
    }

    public void buildHelices(Chain chain) {
        Iterator<Group> residueIterator = chain.residueIterator();

        int helixStartIndex = -1;
        int helixEndIndex = -1;
        while (residueIterator.hasNext()) {
            Group residue = residueIterator.next();
            int residueAbsoluteNumber = residue.getAbsoluteNumber();

            if (residue.getEnvironment().equals(Environment.HELIX_MIDDLE)) {

                // this may be the first residue in the helix core
                if (helixStartIndex == -1) {
                    helixStartIndex = residueAbsoluteNumber - 4;
                    helixEndIndex = residueAbsoluteNumber + 4;

                // otherwise, see if we have to extend the helix
                } else {
                    if (residueAbsoluteNumber > helixEndIndex) {
                        helixEndIndex++;
                    }
                }
            } else {
                if (helixEndIndex != -1 && residueAbsoluteNumber > helixEndIndex) {
                    chain.createHelix(helixStartIndex, helixEndIndex);
                    helixStartIndex = helixEndIndex = -1;
                }
            }
        }
    }

    public void buildStrands(Chain chain) {
        Iterator<Group> residueIterator = chain.residueIterator();

        boolean lastResidueWasStrand = false;
        Segment currentStrand = new Segment(Type.STRAND);
        while (residueIterator.hasNext()) {
            Group residue = residueIterator.next();
            Environment residueEnvironment = residue.getEnvironment();
            if (residueEnvironment.equals(Environment.PARALLEL_STRAND) 
            		|| residueEnvironment.equals(Environment.ANTIPARALLEL_STRAND)) {
                lastResidueWasStrand = true;
                currentStrand.expandBy(residue);
            } else if (residueEnvironment.equals(Environment.LOOP)) {
                if (lastResidueWasStrand) {
                    currentStrand.expandBy(residue);
                } else {
                    if (currentStrand.length() != 0) {
                        chain.addBackboneSegment(currentStrand);
                        currentStrand = new Segment(Type.STRAND);
                    }
                }
                lastResidueWasStrand = false;
            } else {
                if (currentStrand.length() != 0) {
                    chain.addBackboneSegment(currentStrand);
                    currentStrand = new Segment(Type.STRAND);
                }
                lastResidueWasStrand = false;
            }
        }
    }

    public void buildLoops(Chain chain) {
        ListIterator<Segment> itr = chain.segmentListIterator();

        while (itr.hasNext()) {
            Segment backboneSegment = itr.next();
            if (backboneSegment.length() < 2) {
                continue;
            }

            if (itr.hasNext()) {
                Segment nextSegment = itr.next();
                int startOfLoop = backboneSegment.lastResidue().getAbsoluteNumber() + 1;
                int endOfLoop = nextSegment.firstResidue().getAbsoluteNumber() - 1;
                chain.createLoop(startOfLoop, endOfLoop);
            } 
        }
    }

    public static void main(String[] args) {
        try {

            StructureFinder structureFinder = new StructureFinder(args[0]);
            Protein protein = structureFinder.getProtein();
            System.out.println(protein.toString());

            ChainDomainMap cathChainDomainMap = 
            		CATHDomainFileParser.parseUpToParticularID(args[1], protein.getID());
            Map<String, Map<String, String>> chainDomainStringMap = 
            		protein.toTopsDomainStrings(cathChainDomainMap);

            for (String chainID : chainDomainStringMap.keySet()) {
                Map<String, String> domainStrings = chainDomainStringMap.get(chainID);
                for (String domainString : domainStrings.keySet()) {
                    System.out.println(protein.getID() + domainString);
                }
            }

        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
}
