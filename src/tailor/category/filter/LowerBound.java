package tailor.category.filter;

/**
 * Lower bound filter
 */
class LowerBound implements Filter {
    private double minValue;
    private int columnIndex;
    
    public LowerBound(double minValue, int columnIndex) {
        this.minValue = minValue;
        this.columnIndex = columnIndex;
    }
    
    @Override
    public boolean accept(double[] row) {
        return row[columnIndex] >= minValue;
    }
    
    @Override
    public String toString() {
        return "lower bound " + minValue;
    }
}
