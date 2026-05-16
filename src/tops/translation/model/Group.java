package tops.translation.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3d;

import translation.Geometer;

public class Group implements Comparable<Group> {
	private final Map<String, Atom> atomMap;
    private int absoluteNumber;
    private int pdbNumber;
    private String name;
    private PolymerType polymerType;
    private Environment environment;
    private List<HBond> hBonds;
    private double phi;
    private double psi;
    
    private String insertionCode;
    
    private String segmentId;	// TODO

    public Group(int absoluteNumber, int pdbNumber, String name) {
        this.absoluteNumber = absoluteNumber;
        this.pdbNumber = pdbNumber;
        this.atomMap = new HashMap<>();
        this.hBonds = new ArrayList<>();
        this.phi = 0;
        this.psi = 0;
        this.name = name.trim();
        if (this.isBase()) {
            this.polymerType = PolymerType.DNA;
        } else {
            this.polymerType = PolymerType.PROTEIN;
        }
        this.environment = Environment.NONE;
    }
    
    public Group(int pdbNumber, String name) {
    	this(pdbNumber, pdbNumber, name);	// set the absolute number the same as the number in the model
    }
    
    public Group(int pdbNumber, String insertionCode, String name, String segmentId) {
    	this(pdbNumber, name);
    	// these two are extra values in pdb file - could be useful
    	this.insertionCode = insertionCode;
    	this.segmentId = segmentId;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public List<Atom> getAtoms() {
        return new ArrayList<>(this.atomMap.values());
    }

    public int compareTo(Group other) {
        return Integer.valueOf(this.absoluteNumber).compareTo(Integer.valueOf(other.absoluteNumber));
    }

    public boolean isPro() {
        return this.name.equals("PRO");
    }

    public boolean isBase() {
        return this.name.equals("A") || this.name.equals("C") || this.name.equals("G") || this.name.equals("T");
    }

    public boolean isStandardAminoAcid() {
        return  this.name.equals("ALA") ||
                this.name.equals("ARG") ||
                this.name.equals("ASP") ||
                this.name.equals("ASN") ||
                this.name.equals("CYS") ||
                this.name.equals("GLN") ||
                this.name.equals("GLU") ||
                this.name.equals("GLY") ||
                this.name.equals("HIS") ||
                this.name.equals("ILE") ||
                this.name.equals("LEU") ||
                this.name.equals("LYS") ||
                this.name.equals("MET") ||
                this.name.equals("PHE") ||
                this.name.equals("PRO") ||
                this.name.equals("SER") ||
                this.name.equals("THR") ||
                this.name.equals("TRP") ||
                this.name.equals("TYR") ||
                this.name.equals("VAL");
    }

    public boolean isType(PolymerType polymerType) {
        return this.polymerType == polymerType;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public void addHBond(HBond hbond) {
        this.hBonds.add(hbond);
    }

    public List<HBond> getHBonds() {
    	return this.hBonds;
    }

    public List<HBond> getNTerminalHBonds() {
    	List<HBond> nTerminalHBonds = new ArrayList<>();
        for (int i = 0; i < this.hBonds.size(); i++) {
            HBond hBond = this.hBonds.get(i);
            if (hBond.residueIsDonor(this)) {
                //System.out.println("N : " + hBond + " for " + this);
                nTerminalHBonds.add(hBond);
            }
        }
        return nTerminalHBonds;
    }

    public List<HBond> getCTerminalHBonds() {
    	List<HBond> cTerminalHBonds = new ArrayList<>();
        for (int i = 0; i < this.hBonds.size(); i++) {
            HBond hBond = this.hBonds.get(i);
            if (hBond.residueIsAcceptor(this)) {
                //System.out.println("C : " + hBond + " for " + this);
                cTerminalHBonds.add(hBond);
            }
        }
        return cTerminalHBonds;
    }

    public int[] getHBondPartners() {
        int[] partners = new int[this.hBonds.size()];
        for (int i = 0; i < this.hBonds.size(); i++) {
            HBond hbond = hBonds.get(i);
            partners[i] = hbond.getPartner(this).getAbsoluteNumber();
        }
        return partners;
    }

    public boolean bondedTo(Group other) {
        for (int i = 0; i < this.hBonds.size(); i++) {
            HBond hbond = this.hBonds.get(i);
            if (hbond == null) { System.err.println("hbond null"); continue; }
            if (hbond.contains(other)) {
                return true;
            }
        }
        return false;
    }

    public void setPhi(double phi) {
        this.phi = phi;
    }

    public void setPsi(double psi) {
        this.psi = psi;
    }

    public double getPhi() {
        return this.phi;
    }

    public double getPsi() {
        return this.psi;
    }

    public Point3d getCenter() {
        if (this.atomMap.containsKey("CA")) {
            return this.getCoordinates("CA");
        } else {
            return this.calculateCenterOfMass();
        }
    }

    public Point3d calculateCenterOfMass() {
        return Geometer.averagePoints(this.atomMap.values().stream().map(Atom::getCenter).toList());
    }

    public Point3d getCoordinates(String atomType) {
        return this.atomMap.get(atomType).getCenter();
    }

    public int getAbsoluteNumber() {
        return this.absoluteNumber;
    }

    public int getPDBNumber() {
        return this.pdbNumber;
    }
    
    public Integer getNumber() {	// TODO - dedup with above
        return this.pdbNumber;
    }
    
    public Atom getAtom(String atomName) {
    	return atomMap.get(atomName);
    }
    
    public Point3d getAtomPosition(String atomName) {
		return this.atomMap.get(atomName).getCenter();
	}

    public String getType() {
        return this.name;
    }

    public String hBondString() {
        StringBuffer strbuf = new StringBuffer();
       for (HBond hbond : this.hBonds) {
            strbuf.append(hbond).append(" ");
        }
        return strbuf.toString();
    }

    public String toFullString() {
        //return String.format("%s-%-3d %-3d %-19s [%6.2f, %6.2f] %s", this.type, this.pdbNumber, this.absoluteNumber, this.environment, this.phi, this.psi, this.hBondString());
        return "";
    }

    public String toString() {
        //return String.format("%s-%d ", this.type, this.pdbNumber);
        return this.name + " " + this.pdbNumber;
    }

	public void addAtom(Atom atom) {
		this.atomMap.put(atom.getName(), atom);
	}

}
