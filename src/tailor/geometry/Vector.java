package tailor.geometry;

public class Vector {

	private double x;
	private double y;
	private double z;
	
	public Vector() {
	    this.x = 0;
        this.y = 0;
        this.z = 0;
    }
	
	public Vector(Vector other) {
	    this.set(other);
	}
    
	public Vector(String coords) {
		String[] bits = coords.split("\\s+");
		this.x = Double.parseDouble(bits[0]);
		this.y = Double.parseDouble(bits[1]);
		this.z = Double.parseDouble(bits[2]);
	}
	
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double x() {
		return this.x;
	}
	
	public double y() {
		return this.y;
	}

	public double z() {
		return this.z;
	}
	
	public Vector plus(Vector other) {
		return new Vector(this.x + other.x, this.y + other.y, this.z + other.z);
	}
    
    public void add(Vector other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
    }
	
	public Vector minus(Vector other) {
		return new Vector(this.x - other.x, this.y - other.y, this.z - other.z);
	}
	
	public Vector negate() {
		return new Vector(-this.x, -this.y, -this.z);
	}
	
	public Vector divide(double value) {
		return new Vector(this.x / value, this.y / value, this.z / value);
	}
	
	public double multiply(Vector other) {
		return this.x * other.x + this.y * other.y + this.z * other.z;
	}
	
	public double length() {
		return Math.sqrt(this.multiply(this));
	}
	
	public void normalize() {
		Vector normalized = this.divide(this.length());
		this.x = normalized.x;
		this.y = normalized.y;
		this.z = normalized.z;
	}
	
	public double angle(Vector other) {
		try {
			double c = this.multiply(other) / (this.length() * other.length());
			return Math.acos(Math.max(-1.0, Math.min(1.0, c)));
		} catch (ArithmeticException a) {
            System.err.println("arithmetic exception " + a);
			return 0.0;
		}
	}
	
	public Vector cross(Vector other) {
		return new Vector(this.y * other.z - this.z * other.y,
						  this.z * other.x - this.x * other.z,
						  this.x * other.y - this.y * other.x);
	}
	
	public void set(Vector position) {
	    this.x = position.x;
	    this.y = position.y;
	    this.z = position.z;
	}
	
	public String toString() {
		return String.format("[%.3f %.3f %.3f]", this.x, this.y, this.z);
	}
	
}
