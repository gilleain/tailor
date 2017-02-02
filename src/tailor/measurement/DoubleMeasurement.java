package tailor.measurement;

public class DoubleMeasurement implements Measurement {
    
    private final double value;

    public DoubleMeasurement(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

}
