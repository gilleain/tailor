package tailor.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3d;

import tailor.datasource.aigen.ResidueID;

public class Group {
    
    private final Map<String, Atom> atomMap;

    private String name;
    
    private ResidueID  residueId;	// TODO
    
    private int index;
    
    private int number; // TODO - id, index, number...
    

    public Group() {
        this.atomMap = new HashMap<>();
    }
    
    public Group(int residueNumber, String residueName) {
    	this(new ResidueID(residueNumber, ""), residueName, "");
    }
    
    public Group(ResidueID residueId, String residueName) {
    	this(residueId, residueName, "");
    }
    
    public Group(ResidueID residueId, String name, String segmentId) {
    	this();
    	// TODO - store these things
    	this.residueId = residueId;
    	this.name = name;
    }
    

	public Group copy() {
		// TODO could use a copy constructor
		return null;
	}
    
    public void addAtom(Atom atom) {
        this.atomMap.put(atom.getName(), atom);
    }
    
    public Integer getNumber() {
    	return this.residueId.getResseq();
    }
    
    public ResidueID getResidueId() {
    	return this.residueId;
    }
    
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setNumber(int number) {
        this.residueId = new ResidueID(number, "");	// TODO
    }

    public List<Atom> getAtoms() {
        return new ArrayList<>(this.atomMap.values());
    }
    
    public Atom getAtom(String atomName) {
    	return atomMap.get(atomName);
    }

	public Point3d getAtomPosition(String atomName) {
		return getAtom(atomName).getCenter();
	}

    public String toString() {
        return name + atomMap;
    }
}
