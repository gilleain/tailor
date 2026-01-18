package tailor.category;

/**
 * Helper class for mean and standard deviation
 */
class MeanStdDev {
    public double mean;
    public double stdDev;
    
    public MeanStdDev(double mean, double stdDev) {
        this.mean = mean;
        this.stdDev = stdDev;
    }
    
    @Override
    public String toString() {
        return String.format("%4.0f +/- %4.0f", mean, stdDev);
    }
}