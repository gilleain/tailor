package aigen.geometry;


public class Vector {
    public double x;
    public double y;
    public double z;
    
    public static final Vector O = new Vector(0, 0, 0);
    public static final Vector X = new Vector(1, 0, 0);
    public static final Vector Y = new Vector(0, 1, 0);
    public static final Vector Z = new Vector(0, 0, 1);
    
    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public String toString() {
        return String.format("(%.3f %.3f %.3f)", x, y, z);
    }
    
    public Vector negate() {
        return new Vector(-x, -y, -z);
    }
    
    public Vector add(Vector other) {
        return new Vector(x + other.x, y + other.y, z + other.z);
    }
    
    public Vector add(double scalar) {
        return new Vector(x + scalar, y + scalar, z + scalar);
    }
    
    public Vector subtract(Vector other) {
        return new Vector(x - other.x, y - other.y, z - other.z);
    }
    
    public double dot(Vector other) {
        return x * other.x + y * other.y + z * other.z;
    }
    
    public Vector multiply(double scalar) {
        return new Vector(x * scalar, y * scalar, z * scalar);
    }
    
    public Vector divide(double scalar) {
        return new Vector(x / scalar, y / scalar, z / scalar);
    }
    
    public double length() {
        return Math.sqrt(dot(this));
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
            double c = dot(other) / (length() * other.length());
            return Math.acos(Math.max(-1.0, Math.min(1.0, c)));
        } catch (ArithmeticException e) {
            return 0;
        }
    }
    
    public Vector cross(Vector other) {
        return new Vector(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        );
    }
    
    public Vector copy() {
        return new Vector(x, y, z);
    }
}
