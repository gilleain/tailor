package tailor.geometry;


public class Geometry {

	public static final Vector Y = new Vector(0, 1, 0);
	public static final Vector NEG_Y = new Vector(0, -1, 0);
  
    public static double distance(Vector a, Vector b) {
        return a.minus(b).length();
    }
    
    public static double angle(Vector a, Vector b, Vector c) {
        return Math.toDegrees(b.minus(a).angle(b.minus(c)));
    }
    
    public static double torsion(Vector a, Vector b, Vector c, Vector d) {
        Vector l = b.minus(a).cross(c.minus(b));
        Vector r = d.minus(c).cross(b.minus(c));
        double ang = Math.toDegrees(l.angle(r));
        
        if (l.cross(r).dot(c.minus(b)) < 0.0) {
            return -ang;
        } else {
            return ang;
        }
    }
    
    public static Vector makeXYZFromAngle(Vector a, Vector b, double distance, 
                                          double angle, Vector planeVector) {
        double angleRadians = Math.toRadians(180 - angle);
        Vector bA = b.minus(a);
        Vector bANormalised = bA.normalize();
        Vector x = bANormalised.multiply(distance * Math.cos(angleRadians));
        Vector y = planeVector.multiply(distance * Math.sin(angleRadians));
        Vector v = x.add(y);
        return b.add(v);
    }
    
    public static Vector makeXYZ(Vector previousXYZ, double distance, 
                                 Vector previousButOneXYZ, double angle, 
                                 Vector furthestXYZ, double torsion) {
        Vector AB = furthestXYZ.minus(previousButOneXYZ);
        Vector CB = previousXYZ.minus(previousButOneXYZ);
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
	
}
