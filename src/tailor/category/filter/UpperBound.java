package tailor.category.filter;

/**
 * Upper bound filter
 */
class UpperBound implements Filter {
    private double maxValue;
    private int columnIndex;
    
    public UpperBound(double maxValue, int columnIndex) {
        this.maxValue = maxValue;
        this.columnIndex = columnIndex;
    }
    
    @Override
    public boolean accept(double[] row) {
        return row[columnIndex] <= maxValue;
    }
    
    @Override
    public String toString() {
        return "upper bound " + maxValue;
    }
}
