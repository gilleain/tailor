package tailor.measurement;

import tailor.match.Match;

/**
 * A measure is like a ruler - the instrument that you use to make the 
 * measurement. 
 * 
 * @author maclean
 *
 * @param <T> an instance of {@link Measurement} 
 */
public interface Measure<T extends Measurement> {
    
    /**
     * Perform the measurement and return the result
     * 
     * @param match
     * @return
     */
    public T measure(Match match);

    /**
     * Provide a readable name for this particular measurement.
     * 
     * @return 
     */
    public String getName();

}
