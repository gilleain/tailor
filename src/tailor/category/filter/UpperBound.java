package tailor.category.filter;

/**
 * Upper bound filter
 */
class UpperBound implements Filter {
	
    private double maxValue;
    
    public UpperBound(double maxValue) {
        this.maxValue = maxValue;
    }
    
    @Override
    public boolean accept(double value) {
        return value <= maxValue;
    }
    
    @Override
    public String toString() {
        return "upper bound " + maxValue;
    }
}
