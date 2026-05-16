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
    private String type;
    private PolymerType polymerType;
    private Environment environment;
    private List<HBond> hBonds;
    private double phi;
    private double psi;

    public Group() {
        this.atomMap = new HashMap<>();
        this.hBonds = new ArrayList<>();
        this.phi = 0;
        this.psi = 0;
        this.type = "None";
        this.polymerType = PolymerType.NONE;
        this.environment = Environment.NONE;
    }

    public Group(int absoluteNumber, int pdbNumber) {
        this();
        this.absoluteNumber = absoluteNumber;
        this.pdbNumber = pdbNumber;
    }

    public Group(int absoluteNumber, int pdbNumber, String type) {
        this(absoluteNumber, pdbNumber);
        this.type = type.trim();
        if (this.isBase()) {
            this.polymerType = PolymerType.DNA;
        } else {
            this.polymerType = PolymerType.PROTEIN;
        }
    }

    public int compareTo(Group other) {
        return Integer.valueOf(this.absoluteNumber).compareTo(Integer.valueOf(other.absoluteNumber));
    }

    public boolean isPro() {
        return this.type.equals("PRO");
    }

    public boolean isBase() {
        return this.type.equals("A") || this.type.equals("C") || this.type.equals("G") || this.type.equals("T");
    }

    public boolean isStandardAminoAcid() {
        return  this.type.equals("ALA") ||
                this.type.equals("ARG") ||
                this.type.equals("ASP") ||
                this.type.equals("ASN") ||
                this.type.equals("CYS") ||
                this.type.equals("GLN") ||
                this.type.equals("GLU") ||
                this.type.equals("GLY") ||
                this.type.equals("HIS") ||
                this.type.equals("ILE") ||
                this.type.equals("LEU") ||
                this.type.equals("LYS") ||
                this.type.equals("MET") ||
                this.type.equals("PHE") ||
                this.type.equals("PRO") ||
                this.type.equals("SER") ||
                this.type.equals("THR") ||
                this.type.equals("TRP") ||
                this.type.equals("TYR") ||
                this.type.equals("VAL");
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

    public String getType() {
        return this.type;
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
        return this.type + " " + this.pdbNumber;
    }

	public void addAtom(Atom atom) {
		this.atomMap.put(atom.getName(), atom);
	}

}
