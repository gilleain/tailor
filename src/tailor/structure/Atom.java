package tailor.structure;

import java.util.List;

import tailor.geometry.Vector;

public class Atom implements Structure {
    
    private final Level level = Level.ATOM;
    
    private final String name;
    
    private final Vector center;
    
    // from pdb file
    private Double bFactor;
    private double occupancy;
    private String altloc;
    
    public Atom(String name) {
        this.name = name;
        this.center = new Vector();
    }
    
    public Atom(String name, Vector center) {
        this.name = name;
        this.center = center;
    }

    public Atom(String name, double[] coord, double bFactor, double occupancy, String altloc) {
		// TODO Auto-generated constructor stub
    	this.name = name;
    	this.center = new Vector(coord[0], coord[1], coord[2]);
    	this.bFactor = bFactor;
    	this.occupancy = occupancy;
    	this.altloc = altloc;
	}
    
    public Atom copy() {
    	// TODO - could use copy constructor
    	return null;
    }

	public Vector getCenter() {
        return center;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(StructureVisitor visitor) {
        visitor.visit(this);
    }
    
    public void accept(HierarchyVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public List<Structure> getSubstructures() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addSubStructure(Structure structure) {
        // can never add any substructure to an atom
        throw new UnsupportedOperationException();
    }

    public void setPosition(Vector position) {
        this.center.set(position);
    }
    
    public String toString() {
        return name + " " + center;
    }
}
