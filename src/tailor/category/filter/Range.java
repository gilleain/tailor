package tailor.category.filter;



/**
 * A closed bound [min max]
 * 
 * @author maclean
 *
 */
public class Range implements Filter {
    
    private double min;
    private double max;

    /**
     * @param min
     * @param max
     */
    public Range(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public boolean accept(double value) {
        return this.min < value && this.max > value;
    }
    
    @Override
    public String toString() {
        return String.format("range (%s - %s)", min, max);
    }

}
