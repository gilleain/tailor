package aigen.geometry;

public class Geometry {
	
	public static final Vector Y = new Vector(0, 1, 0);
	public static final Vector NEG_Y = new Vector(0, -1, 0);
  
    public static double distance(Vector a, Vector b) {
        return a.subtract(b).length();
    }
    
    public static double angle(Vector a, Vector b, Vector c) {
        return Math.toDegrees(b.subtract(a).angle(b.subtract(c)));
    }
    
    public static double torsion(Vector a, Vector b, Vector c, Vector d) {
        Vector l = b.subtract(a).cross(c.subtract(b));
        Vector r = d.subtract(c).cross(b.subtract(c));
        double ang = Math.toDegrees(l.angle(r));
        
        if (l.cross(r).dot(c.subtract(b)) < 0.0) {
            return -ang;
        } else {
            return ang;
        }
    }
    
    public static Vector makeXYZFromAngle(Vector a, Vector b, double distance, 
                                          double angle, Vector planeVector) {
        double angleRadians = Math.toRadians(180 - angle);
        Vector bA = b.subtract(a);
        Vector bANormalised = bA.normalize();
        Vector x = bANormalised.multiply(distance * Math.cos(angleRadians));
        Vector y = planeVector.multiply(distance * Math.sin(angleRadians));
        Vector v = x.add(y);
        return b.add(v);
    }
    
    public static Vector makeXYZ(Vector previousXYZ, double distance, 
                                 Vector previousButOneXYZ, double angle, 
                                 Vector furthestXYZ, double torsion) {
        Vector AB = furthestXYZ.subtract(previousButOneXYZ);
        Vector CB = previousXYZ.subtract(previousButOneXYZ);
        Vector normalAC = AB.cross(CB);
        
        if (normalAC.length() == 0.0) {
            return makeXYZFromAngle(previousButOneXYZ, previousXYZ, distance, angle, Vector.Y);
        }
        
        Vector normalBC = CB.cross(normalAC);
        
        double torsionRadians = Math.toRadians(torsion);
        Vector r = normalAC.normalize().multiply(Math.cos(torsionRadians))
                   .add(normalBC.normalize().multiply(Math.sin(torsionRadians)));
        Vector newFrameOfReference = CB.normalize().cross(r);
        
        double angleRadians = Math.toRadians(angle);
        Vector DC = CB.normalize().multiply(-Math.cos(angleRadians))
                    .add(newFrameOfReference.multiply(Math.sin(angleRadians)));
        DC = DC.multiply(distance);
        
        return previousXYZ.add(DC);
    }
    
    public static double invertTorsion(double torsion) {
        if (torsion < 0) {
            return 180 + torsion;
        } else {
            return torsion - 180;
        }
    }
    
    public static void main(String[] args) {
        Vector v1 = new Vector(0, 0, 1);
        Vector v2 = new Vector(0, 1, 0);
        Vector v3 = new Vector(1, 0, 0);
        Vector v4 = new Vector(1, 1, 1);
        
        System.out.println(v1.add(v2));
        System.out.println(v2.subtract(v3));
        System.out.println(v1.dot(v3));
        System.out.println(v1.divide(3));
        System.out.println(v2.length());
        System.out.println(v4.copy());
        System.out.println(v1.cross(v3));
        
        v4 = v4.normalize();
        System.out.println(v4);
        
        System.out.println(distance(v1, v2));
        System.out.println(angle(v1, v2, v3));
        System.out.println(torsion(v1, v2, v3, v4));
    }
}
