package tops.translation.model;

import javax.vecmath.Point3d;

public class Atom {
    
    private final String name;
    
    private final Point3d center;
    
    // from pdb file
    private Double bFactor;
    private double occupancy;
    private String altloc;
    
    public Atom(String name) {
        this.name = name;
        this.center = new Point3d();
    }
    
    public Atom(String name, Point3d center) {
        this.name = name;
        this.center = center;
    }

    public Atom(String name, double[] coord, double bFactor, double occupancy, String altloc) {
		// TODO Auto-generated constructor stub
    	this.name = name;
    	this.center = new Point3d(coord[0], coord[1], coord[2]);
    	this.bFactor = bFactor;
    	this.occupancy = occupancy;
    	this.altloc = altloc;
	}
    
    public Atom(Atom other) {
    	this(other.name, other.center);
    }

	public Point3d getCenter() {
        return center;
    }

    public String getName() {
        return name;
    }

    public void setPosition(Point3d position) {
        this.center.set(position);
    }
    
    public String toString() {
        return name + " " + center;
    }
}
