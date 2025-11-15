package tailor.category.aigen;


/**
 * Range condition
 */
public class Range implements Condition {
    private double min;
    private double max;
    
    public Range(double min, double max) {
        this.min = min;
        this.max = max;
    }
    
    @Override
    public boolean satisfiedBy(double value) {
        return min < value && value < max;
    }
    
    @Override
    public String toString() {
        return String.format("range (%s - %s)", min, max);
    }
}
