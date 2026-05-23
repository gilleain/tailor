package tailor.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import tailor.geometry.Axis;
import tailor.geometry.Geometer;
import tailor.structure.Segment.Type;

public class Chain {
    private String name;
    private PolymerType type;
    private Point3d center;
    private List<Group> residues;
    private List<HBond> hbonds;
    private List<Sheet> sheets;
    private List<Segment> segments;
    private List<Edge> chiralities;
    private List<Domain> domains;

    public Chain() {
        this("A");	// TODO - is this a sensible default?
    }

    public Chain(String label) {
    	this(label, PolymerType.NONE);
    }
    
    public Chain(String name, PolymerType type) {
    	this.residues = new ArrayList<>();
        this.hbonds = new ArrayList<>();
        this.sheets = new ArrayList<>();
        this.segments = new ArrayList<>();
        this.chiralities = new ArrayList<>();
        this.domains = new ArrayList<>();
        this.center = null;
        this.name = name;
        this.type = type;
    }
    
    public void addGroup(Group group) {
    	this.residues.add(group);
    }
    
    public List<Group> getGroups() {
    	return this.residues;
    }

    public int length() {
        return this.residues.size();
    }
    
    public Point3d getCenter() {
    	return this.center;
    }
    
    public PolymerType getType() {
    	return this.type;
    }
    
    public List<Domain> getDomains() {
    	return this.domains;
    }

    public boolean isDNA() {
        return this.residues.get(0).isType(PolymerType.DNA);
    }

    // since this doesn't check, in the event that a structure actually HAS
    // backbone amide hydrogens, it will overwrite them...
    public void addBackboneAmideHydrogens() {
        for (int i = 1; i < this.residues.size(); i++) {
            Group lastResidue = this.residues.get(i - 1);
            Group residue     = this.residues.get(i);

            if (residue.isPro()) {
                continue;
            }

            Point3d lastC       = lastResidue.getCoordinates("C");
            Point3d lastO       = lastResidue.getCoordinates("O");
            Point3d currN       =     residue.getCoordinates("N");
            if (lastC == null || lastO == null) return;	// TODO - handle non-protein chains better ...

            Vector3d OC = new Vector3d();
            OC.sub(lastC, lastO);
            OC.normalize();

            Point3d hPos = new Point3d();
            hPos.add(currN, OC);

            residue.addAtom(new Atom("H", hPos));
        }
    } 

    public Group createResidue(int pdbNumber, String residueType) {
        Group residue = new Group(this.residues.size(), pdbNumber, residueType);
        this.residues.add(residue);
        return residue;
    }

    public void createHelix(int helixStartIndex, int helixEndIndex) {
    	Group firstResidue = this.getResidueByAbsoluteNumbering(helixStartIndex);
        Segment helix = new Segment(Type.HELIX, firstResidue);
        for (int index = helixStartIndex + 1; index < helixEndIndex + 1; index++) {
            helix.expandBy(this.getResidueByAbsoluteNumbering(index));
        }
        //System.err.println("Created " + helix + " from " + helixStartIndex + " " + helixEndIndex);
        this.segments.add(helix);
    }

    public void createStrand(int strandStartIndex, int strandEndIndex) {
    	Group firstResidue = this.getResidueByAbsoluteNumbering(strandStartIndex);
        Segment strand = new Segment(Type.STRAND, firstResidue);
        for (int index = strandStartIndex + 1; index < strandEndIndex + 1; index++) {
            strand.expandBy(this.getResidueByAbsoluteNumbering(index));
        }
        //System.err.println("Created " + strand + " from " + strandStartIndex + " " + strandEndIndex);
        this.segments.add(strand);
    }

    public void createLoop(int startIndex, int endIndex) {
    	Group firstResidue = this.getResidueByAbsoluteNumbering(startIndex);
        Segment unstructured = new Segment(Type.UNSTRUCTURED, firstResidue);
        for (int index = startIndex + 1; index < endIndex + 1; index++) {
            unstructured.expandBy(this.getResidueByAbsoluteNumbering(index));
        }
        this.segments.add(unstructured);
    }

    // firstly, assume that the sses are sorted so that for i < j; i.end < j.end
    // secondly assume that, if two helices overlap, they should be merged
    public void mergeHelices() {
        for (int segmentIndex = 0; segmentIndex < this.segments.size() - 1; segmentIndex++) {
            Segment sseA = this.segments.get(segmentIndex);
            if (sseA.getType() == Type.HELIX) {
                Segment sseB = this.segments.get(segmentIndex + 1);
                if (sseB.getType() == Type.HELIX) {
                    if (sseA.overlaps(sseB)) {
                        //System.err.println("Merging : " + sseA + " and " + sseB); 
                        sseB.mergeWith(sseA);

                        //System.err.println("Removing: " + sseA); 
                        this.segments.remove(segmentIndex);
                        segmentIndex--;
                    } else {
                        //System.err.println("No overlap : " + sseA + " and " + sseB); 
                    }
                } 
            }
        }
    }

    public void sortSegments() {
        Collections.sort(this.segments);
    }

    public List<Segment> getSegments() {
        return this.segments;
    }

    public boolean hasResidueByPDBNumbering(int pdbResidueNumber) {
        for (Group residue : this.residues) {
            if (residue.getPDBNumber() == pdbResidueNumber) {
                return true;
            }
        }
        return false;
    }

    public Group getResidueByPDBNumbering(int pdbResidueNumber) {
    	for (Group residue : this.residues) {
            if (residue.getPDBNumber() == pdbResidueNumber) {
                return residue;
            }
        }
        return null;
    }

    public Group getResidueByAbsoluteNumbering(int i) {
        return this.residues.get(i);
    }

    public boolean hasResidueByAbsoluteNumbering(int i) {
        return i < this.residues.size();
    }

    public Group firstResidue() {
        return this.residues.get(0);
    }

    public Group lastResidue() {
        return this.residues.get(this.residues.size() - 1);
    }

    public Group getNextResidue(int residueIndex) {
        if (residueIndex + 1 >= this.residues.size()) {
            return null;
        } else {
            return this.residues.get(residueIndex + 1);
        }
    }

    public Iterator<Group> residueIterator(int indexFrom) {
        return this.residues.subList(indexFrom, this.residues.size() - 1).iterator();
    }

    public Iterator<HBond> hbondIterator() {
        Collections.sort(this.hbonds);
        return this.hbonds.iterator();
    }

    public void addSheet(Sheet sheet) {
        this.sheets.add(sheet);
    }

    public void createSheet(Segment strand, Segment otherStrand) {
        if (this.sheets.isEmpty()) {
            this.sheets.add(new Sheet(1, strand, otherStrand));
        } else {
            int lastSheetNumber = this.sheets.get(this.sheets.size() - 1).getNumber();
            this.sheets.add(new Sheet(lastSheetNumber + 1, strand, otherStrand));
        }
    }

    public void removeSheet(Sheet sheet) {
        this.sheets.remove(sheet);
    }

    public Sheet getSheetContaining(Segment strand) {
    	for (Sheet sheet : this.sheets) {
            if (sheet.contains(strand)) {
                return sheet;
            }
        }
        return null;
    }

    public Iterator<Sheet> sheetIterator() {
        return this.sheets.iterator();
    }

    public int numberOfSheets() {
        return this.sheets.size();
    }

    public void addChirality(Segment first, Segment second, char chirality) {
        this.chiralities.add(new Edge(first, second, chirality));
    }

    public Axis getAxis() {
        List<Point3d> centroids = new ArrayList<>();
        for (Segment nextBackboneSegment : this.segments) {
            Axis a = nextBackboneSegment.getAxis();
            //System.out.println("Centroid for BS " + nextBackboneSegment + " is " + a.getCentroid());
            centroids.add(a.getCentroid());
        }
        //System.out.println("averagePoints = " + Geometer.averagePoints(centroids));
        Axis axis = Geometer.leastSquareAxis(centroids);

        //this is a slight hack to get start and end coordinates into the chain axis
        //might be better to use the N terminus as the start?
        Point3d start = new Point3d(axis.getCentroid());
        start.scale(5, axis.getAxisVector());
        axis.setStart(start);
        Point3d end = new Point3d(axis.getCentroid());
        end.scale(-5, axis.getAxisVector());
        axis.setEnd(end);

        return axis;
    }

    public void addHBond(HBond hbond) {
        this.hbonds.add(hbond);
    }

    public String getCathCompatibleName() {
        if (this.name.equals("  ")) {
            return "0";
        } else {
            return this.name.trim();
        }
    }

    public String getName() {
        return this.name;
    }

    public void addSegment(Segment segment) {
        this.segments.add(segment);
    }

    public ListIterator<Segment> segmentListIterator() {
        return this.segments.listIterator();
    }

    public ListIterator<Segment> segmentListIterator(Segment segment) {
        return this.segments.listIterator(this.segments.indexOf(segment));
    }

    public ListIterator<Segment> segmentListIterator(Segment startSegment, Segment endSegment) {
        return this.segments.subList(this.segments.indexOf(startSegment) + 1, this.segments.indexOf(endSegment)).listIterator();
    }

    public int numberOfSegmentsInBetween(Segment startSegment, Segment endSegment) {
        return this.segments.indexOf(endSegment) - (this.segments.indexOf(startSegment) + 1);
    }
    
    public List<Segment> filterSegmentsByDomain(Domain domain) {
        return domain.filter(this.segments);
    }

    public void calculateTorsions() {
        Iterator<Group> residueIterator = this.residues.iterator();
        Group previousResidue = null;
        Group thisResidue = null;

        if (residueIterator.hasNext()) {
            previousResidue = (Group) residueIterator.next();
        } else {
            return;
        }

        if (residueIterator.hasNext()) {
            thisResidue = (Group) residueIterator.next();
        } else {
            return;
        }

        while (residueIterator.hasNext()) {
            Point3d prevC = previousResidue.getCoordinates("C");
            Point3d thisN = thisResidue.getCoordinates("N");
            Point3d thisCA = thisResidue.getCoordinates("CA");
            Point3d thisC = thisResidue.getCoordinates("C");

            double phi = Geometer.torsion(prevC, thisN, thisCA, thisC);
            thisResidue.setPhi(phi);

            previousResidue = thisResidue;
            if (residueIterator.hasNext()) {
                Group nextResidue = (Group) residueIterator.next();

                Point3d nextN = nextResidue.getCoordinates("N");

                double psi = Geometer.torsion(thisN, thisCA, thisC, nextN);
                thisResidue.setPsi(psi);

                thisResidue = nextResidue;
            } else {
                break;
            }
        }
    }

    public void findOrientations() {
        Axis chainAxis = this.getAxis();

        for (Segment nextSegment : this.segments) {
            nextSegment.determineOrientation(chainAxis);
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Chain : " + this.getName() + " residue " + this.firstResidue().getPDBNumber() + " to " + this.lastResidue().getPDBNumber() + "\n");

        for (Group r : this.residues) {
            s.append(r.toFullString());
            s.append("\n");
        }

        for (HBond nextBond : this.hbonds) {
            s.append(nextBond + "\n");
        }

        for (Segment nextBackboneSegment : this.segments) {
            s.append(nextBackboneSegment.toFullString() + "\n");
        }

        for (Sheet sheet : this.sheets) {
//            System.out.println("Sheet paths " + sheet.getSheetPaths());
            s.append(sheet + "\n");
        }

        return s.toString();
    }
    
    public List<Edge> getChiralities() {
    	return this.chiralities;
    }

	public List<Sheet> getSheets() {
		return this.sheets;
	}
}
