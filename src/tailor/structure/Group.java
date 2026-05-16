package tailor.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3d;

import tops.translation.model.Atom;

public class Group {
    
    private final Map<String, Atom> atomMap;

    private String name;
    
    private int residueSeq;
    
    private String insertionCode;
    
    private String segmentId;	// TODO
    
    public Group() {
        this.atomMap = new HashMap<>();
    }
    
    public Group(int residueNumber, String residueName) {
    	this(residueNumber, "", residueName, "");
    }
    
    public Group(int residueSeq, String insertionCode, String residueName) {
    	this(residueSeq, insertionCode, residueName, "");
    }
    
    public Group(int residueSeq, String insertionCode, String name, String segmentId) {
    	this();
    	this.residueSeq = residueSeq;
    	this.insertionCode = insertionCode;
    	this.name = name;
    	this.segmentId = segmentId;
    }
    
    public void addAtom(Atom atom) {
        this.atomMap.put(atom.getName(), atom);
    }
    
    public Integer getNumber() {
    	return this.residueSeq;
    }

    public String getName() {
        return name;
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
