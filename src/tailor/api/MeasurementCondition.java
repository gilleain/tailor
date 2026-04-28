package tailor.api;

/**
 * Conditions on a measurement.
 */
public interface MeasurementCondition {
	
	
	/**
	 * Check if this measurement is accepted by this condition.
	 * 
	 * @param measurement
	 * @return true if accepted
	 */
	boolean accept(Measurement measurement);


}
