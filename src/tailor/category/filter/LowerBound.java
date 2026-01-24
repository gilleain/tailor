package tailor.category.filter;

/**
 * Lower bound filter
 */
class LowerBound implements Filter {
	
    private double minValue;
    
    public LowerBound(double minValue) {
        this.minValue = minValue;
    }
    
    @Override
    public boolean accept(double value) {
        return value >= minValue;
    }
    
    @Override
    public String toString() {
        return "lower bound " + minValue;
    }
}
