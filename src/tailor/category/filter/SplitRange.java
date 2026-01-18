package tailor.category.filter;

/**
 * Split range condition (wraps around at boundaries)
 */
class SplitRange implements Filter {
	private int columnIndex;
    private double lower;
    private double upper;
    
    public SplitRange(int columnIndex, double lower, double upper) {
        this.lower = lower;
        this.upper = upper;
    }
    
    @Override
    public boolean accept(double[] values) {
    	double value = values[columnIndex];
        return (-180 < value && value < lower) || (upper < value && value < 180);
    }
    
    @Override
    public String toString() {
        return String.format("splitrange (%s - %s)", lower, upper);
    }
}
