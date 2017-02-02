package tailor.measurement;

public class HBondMeasurement implements Measurement {

    private final double haDistance;
    
    private final double dhaAngle;

    private final double haaAngle;

    public HBondMeasurement(double haDistance, double dhaAngle, double haaAngle) {
        this.haDistance = haDistance;
        this.dhaAngle = dhaAngle;
        this.haaAngle = haaAngle;
    }
    
    public double getHaDistance() {
        return haDistance;
    }
    
    public double getDhaAngle() {
        return dhaAngle;
    }
    
    public double getHaaAngle() {
        return haaAngle;
    }

    public String toString() {
        return "HBond Measurement";
    }

}
