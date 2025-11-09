package tailor.geometry;


public class Vector {

	private double x;
	private double y;
	private double z;

	public static final Vector O = new Vector(0, 0, 0);
	public static final Vector X = new Vector(1, 0, 0);
	public static final Vector Y = new Vector(0, 1, 0);
	public static final Vector Z = new Vector(0, 0, 1);
	
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

	public double dot(Vector other) {
		return x * other.x + y * other.y + z * other.z;
	}
	
	public Vector plus(Vector other) {
		return new Vector(this.x + other.x, this.y + other.y, this.z + other.z);
	}

	public Vector add(double scalar) {
		return new Vector(x + scalar, y + scalar, z + scalar);
	}
	
	public Vector add(Vector other) {
        return new Vector(x + other.x, y + other.y, z + other.z);
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

	public Vector multiply(double scalar) {
		return new Vector(x * scalar, y * scalar, z * scalar);
	}
	
	public double length() {
		return Math.sqrt(this.multiply(this));
	}

	public Vector normalize() {
		double len = length();
		if (len == 0.0) {
			return new Vector(x, y, z);
		}
		return divide(len);
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
