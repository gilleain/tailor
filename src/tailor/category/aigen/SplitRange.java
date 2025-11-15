package tailor.category.aigen;


/**
 * Split range condition (wraps around at boundaries)
 */
class SplitRange implements Condition {
    private double lower;
    private double upper;
    
    public SplitRange(double lower, double upper) {
        this.lower = lower;
        this.upper = upper;
    }
    
    @Override
    public boolean satisfiedBy(double value) {
        return (-180 < value && value < lower) || (upper < value && value < 180);
    }
    
    @Override
    public String toString() {
        return String.format("splitrange (%s - %s)", lower, upper);
    }
}
