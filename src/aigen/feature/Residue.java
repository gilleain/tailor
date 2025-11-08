package aigen.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import aigen.geometry.Vector;

public class Residue extends Feature {
    private ResidueID residueID;
    private String resname;
    private String segID;

    public Residue(Object residueID, String resname) {
        this(residueID, resname, null);
    }

    public Residue(Object residueID, String resname, String segID) {
        super();
        if (residueID instanceof ResidueID) {
            this.residueID = (ResidueID) residueID;
        } else if (residueID instanceof Integer) {
            this.residueID = new ResidueID((Integer) residueID, "");
        } else {
            throw new IllegalArgumentException("Invalid residueID type");
        }
        this.resname = resname;
        this.segID = segID;
        this.levelCode = "R";
    }

    public int getNumber() {
        return residueID.number;
    }

    public Atom getAtom(String atomName) throws IllegalArgumentException {
        for (Feature a : this) {
            if (a instanceof Atom) {
                Atom atom = (Atom) a;
                if (atom.getName().equals(atomName)) {
                    return atom;
                }
            }
        }
        throw new IllegalArgumentException("No atom with name " + atomName);
    }
    
    public String getResname() {
      return resname;
    }
    
    public List<Atom> getSubFeatures() {
    	List<Atom> atoms = new ArrayList<>();
    	for (Feature a : this) {
            if (a instanceof Atom) {
                atoms.add((Atom) a);
            }
    	}
                
    	return atoms;
    }


    public Vector getAtomPosition(String atomName) {
        return getAtom(atomName).getAtomCenter();
    }

    public String toFullString() {
        return toString() + subFeatures.toString();
    }

    @Override
    public String toString() {
        return String.format("%s%d%s", resname, residueID.number, residueID.insertionCode);
    }

    static class ResidueID {
        int number;
        String insertionCode;

        public ResidueID(int number, String insertionCode) {
            this.number = number;
            this.insertionCode = insertionCode;
        }
    }

	public List<Atom> getAtoms() {
		return this.subFeatures.stream()
				.filter(f -> f instanceof Atom).map(x -> (Atom)x).collect(Collectors.toList());
	}
}