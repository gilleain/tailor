package tops.translation.model;

import java.util.Iterator;


public class Helix extends RepetitiveStructure {
    private static int phiMin = -110;
    private static int phiMax = -30;
    private static int psiMin = -80;
    private static int psiMax = -20;

    public Helix() {
        super();
    }

    public Helix(Residue first) {
        super(first);
    }

    public char getTypeChar() {
        if (this.orientation.equals("UP")) {
            return 'H';
        } else {
            return 'h';
        }
    }

    public static boolean torsionsMatch(Residue r) {
        return RepetitiveStructure.torsionsMatch(r, Helix.phiMin, Helix.phiMax, Helix.psiMin, Helix.psiMax);
    }

    public static boolean hbondsMatch(Residue r) {
        Iterator<HBond> iterator = r.getHBondIterator();
        while (iterator.hasNext()) {
            HBond hbond = (HBond) iterator.next();
            if (hbond.hasHelixResidueSeparation()) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return "Helix (" + this.number + ") : " + this.firstResidue() + " - " + this.lastResidue();
    }

    public String toFullString() {
        return "Helix (" + this.number + ") : " + this.orientation + " " + this.firstResidue() + " - " + this.lastResidue() + " " + this.axis;
    }

	@Override
	public String toCompactString() {
		return "H(" + this.number + ")[" + this.firstResidue().getPDBNumber() + ":" + this.lastResidue().getPDBNumber() + "]";
	}

	@Override
	public Type getType() {
		return Type.HELIX;
	}
}
