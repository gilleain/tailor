package tailor.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import javax.vecmath.Point3d;

import tailor.geometry.Axis;

public class Segment implements Comparable<Segment> {
	
	// TODO - not exhaustive of possible types, 
	// 'OTHER' is doing a lot of heavy lifting here 
	public enum Type {
		NTERMINUS("N"),
		CTERMINUS("C"),
		STRAND("E"),
		HELIX("H"),
		UNSTRUCTURED("U");
		
		private final String typeString;
		Type(String typeString) {
			this.typeString = typeString;
		}
		public String getTypeString() {
			return typeString;
		}
	}
	
	public enum Orientation {
		UP,
		DOWN,
		NONE;
	}

    private int number;

    private TreeSet<Group> residues;

    private Axis axis;

    private Orientation orientation;
    
    private Type type;

    public Segment(Type type) {
    	this.type = type;
        this.residues = new TreeSet<>();
        this.axis = null;
        this.orientation = Orientation.NONE;
    }

    public Segment(Type type, Group first) {
        this(type);
        this.residues.add(first);
    }

    public String toFullString() {
    	if (this.type == Type.HELIX) {
    		 return "Helix (" + this.number + ") : " + this.orientation 
    				 + " " + this.firstResidue() + " - " + this.lastResidue() + " " + this.axis;
    	} else if (this.type == Type.STRAND) {
    		return "Strand (" + this.number + ") : " + this.orientation + " " 
    				+ this.firstResidue() + " - " + this.lastResidue() + " " + this.axis;
    	} else if (this.type == Type.NTERMINUS) {
    		return "N-Terminus";
    	} else if (this.type == Type.CTERMINUS) {
    		return "C-Terminus";
    	} else if (this.type == Type.UNSTRUCTURED) {
    		return "Unstuctured (" + this.number + ") : " + this.orientation 
   				 + " " + this.firstResidue() + " - " + this.lastResidue() + " " + this.axis;
    	} else {
    		return "";
    	}
    	
    }
    
    public String toCompactString() {
    	if (this.type == Type.HELIX) {
    		return "H(" + this.number + ")[" 
    				+ this.firstResidue().getPDBNumber() + ":" + this.lastResidue().getPDBNumber() + "]";
    	} else if (this.type == Type.STRAND) {
    		return "E(" + this.number + ")[" 
    				+ this.firstResidue().getPDBNumber() + ":" + this.lastResidue().getPDBNumber() + "]";
    	} else if (this.type == Type.NTERMINUS) {
    		return "N";
    	} else if (this.type == Type.CTERMINUS) {
    		return "C";
    	} else if (this.type == Type.UNSTRUCTURED) {
    		return "U(" + this.number + ")[" 
    				+ this.firstResidue().getPDBNumber() + ":" + this.lastResidue().getPDBNumber() + "]";
    	} else {
    		return "";
    	}
    	
    }

    public Type getType() {
    	return this.type;
    }
    
    public boolean isType(Type type) {
    	return this.type == type;
    }

    public int compareTo(Segment other) {
        try {
            return this.firstResidue().compareTo(other.firstResidue());
        } catch (NoSuchElementException n) {
            return 0;
        }
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return this.number;
    }

    public int length() {
        return this.residues.size();
    }

    public int firstPDB() {
        try {
            return this.firstResidue().getPDBNumber();
        } catch (NoSuchElementException n) {
            return -1;
        }
    }

    public int lastPDB() {
        try {
            return this.lastResidue().getPDBNumber();
        } catch (NoSuchElementException n) {
            return -1;
        }
    }

    public Group firstResidue() throws NoSuchElementException {
        return this.residues.first();
    }

    public Group lastResidue() throws NoSuchElementException {
        return this.residues.last();
    }

    public Iterator<Group> iterator() {
        return this.residues.iterator();
    }
    
    public Group getResidueByAbsoluteNumber(int number) {
        for (Group residue : this.residues) {
            if (residue.getAbsoluteNumber() == number) {
                return residue;
            }
        }
        return null;
    }

    public boolean bondedTo(Group otherResidue) {
        for (Group residue : this.residues) {
            if (residue.bondedTo(otherResidue)) {
                return true;
            }
        }
        return false;
    }

    public boolean continuousWith(Segment other) {
        if (this.getClass() == other.getClass()) {
            if (this.getAxis().approximatelyLinearTo(other.getAxis())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void mergeWith(Segment other) {
        this.residues.addAll(other.residues);
        this.axis = null;
    }

    public boolean overlaps(Segment other) {
        try {
            int oS = other.firstResidue().getAbsoluteNumber();
            int oE = other.lastResidue().getAbsoluteNumber();
            return this.containsAbsoluteNumber(oS)
                    || this.containsAbsoluteNumber(oE);
        } catch (NoSuchElementException n) {
            return false;
        }
    }

    public List<Point3d> getCAlphaCoordinates() {
        List<Point3d> cAlphas = new ArrayList<>();
        Iterator<Group> itr = this.residues.iterator();
        while (itr.hasNext()) {
            Group nextResidue = itr.next();
            cAlphas.add(nextResidue.getCoordinates("CA"));
        }
        return cAlphas;
    }

    public char getTopsSymbol() {
    	char typeChar = this.getType().getTypeString().charAt(0);
        if (this.getOrientation() == Orientation.UP) {
            return Character.toUpperCase(typeChar);
        } else {
            return Character.toLowerCase(typeChar);
        }
    }

    public Orientation getOrientation() {
        return this.orientation;
    }

    public void expandBy(Group r) {
        this.residues.add(r);
    }

    public boolean containedInPDBNumberRange(int pdbResidueNumberStart,
            int pdbResidueNumberEnd) {
        try {
            return this.firstPDB() >= pdbResidueNumberStart
                    && this.lastPDB() <= pdbResidueNumberEnd;
        } catch (NoSuchElementException n) {
            return false;
        }
    }
    
    public boolean overlapsPDBNumberRange(int pdbResidueNumberStart, int pdbResidueNumberEnd) {
        try {
            int first = this.firstPDB();
            int last = this.lastPDB();
            return (first >= pdbResidueNumberStart && first <= pdbResidueNumberEnd) ||
                    (last >= pdbResidueNumberStart && last <= pdbResidueNumberEnd);
        } catch (NoSuchElementException n) {
            return false;
        }
    }

    public boolean containsPDBNumber(int pdbResidueNumber) {
        for (Group r : this.residues) {
            if (r.getPDBNumber() == pdbResidueNumber) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAbsoluteNumber(int absoluteResidueNumber) {
        try {
            Group first = this.firstResidue();
            Group last = this.lastResidue();
            return first.getAbsoluteNumber() <= absoluteResidueNumber
                    && last.getAbsoluteNumber() >= absoluteResidueNumber;
        } catch (NoSuchElementException n) {
            return false;
        }
    }

    public boolean contains(Group r) {
        return this.containsAbsoluteNumber(r.getAbsoluteNumber());
    }

    public Axis getAxis() {
        if (this.axis == null) {
            this.calculateAxis();
        }
        return this.axis;
    }

    public void calculateAxis() {
        // this.axis = Geometer.leastSquareAxis(this.getCAlphaCoordinates());
        // if this segment is only one or no residues, make a zero axis
        if (this.residues.size() < 2) {
            this.axis = new Axis();
            return;
        }

        // otherwise, diff the centers of the first and last residues
        try {
            Point3d start = this.firstResidue().getCenter();
            Point3d end = this.lastResidue().getCenter();
            this.axis = new Axis(start, end);
            this.axis.setStart(start);
            this.axis.setEnd(end);
//            System.err.println("Calculated axis for " + this.toCompactString() + " as " + this.axis);
        } catch (NoSuchElementException n) {
            // do nothing?
            System.out.println("NoSuchElementException for " + this);
        }
        // System.out.println("setting axis of " + this + " to : " + this.axis);
    }

    // the axis we pass into the function is considered to be "UP"
    public void determineOrientation(Axis axis) {
        double angle = this.getAxis().angle(axis);
        // System.out.println("Angle of " + this + " with axis " + axis + " is "
        // + angle);
        if (angle > 90) {
            this.orientation = Orientation.DOWN;
        } else {
            this.orientation = Orientation.UP;
        }
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public char getRelativeOrientation(Segment other) {
        return this.getRelativeOrientation(other.getAxis());
    }

    public char getRelativeOrientation(Axis other) {
        double angle = this.getAxis().angle(other);
        // System.out.println("angle between " + this + " and " + other + " = "
        // + angle);
        if (angle > 90) {
            return 'A';
        } else {
            return 'P';
        }
    }

    public List<Group> getResidues() {
        List<Group> residueList = new ArrayList<>();
        for (Group residue : this.residues) {
            residueList.add(residue);
        }
        return residueList;
    }

	public void addGroup(Group group) {
		residues.add(group);
	}
}
