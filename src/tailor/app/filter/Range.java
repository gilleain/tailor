package tailor.app.filter;



/**
 * A closed bound [min max]
 * 
 * @author maclean
 *
 */
public class Range implements Filter {
    
    private int columnIndex;
    private double min;
    private double max;

    /**
     * @param columnIndex The index in the table of the column to filter.
     * @param min
     * @param max
     */
    public Range(int columnIndex, double min, double max) {
        this.columnIndex = columnIndex;
        this.min = min;
        this.max = max;
    }

    public boolean accept(double[] values) {
        return this.min < values[this.columnIndex] && this.max > values[this.columnIndex];
    }

}
