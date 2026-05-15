package tailor.api;

import tops.translation.experimental.SegmentDescription;
import tops.translation.model.Segment;

/**
 * Measurement to make on a single segment.
 */
public interface SegmentIntPropertyMeasure {
	
	Measurement<Integer> measure(Segment segment);

	/**
	 * @return the segment description the measure applies to
	 */
	SegmentDescription getSegmentDescription();

	/**
	 * @return a descriptive name for the measure
	 */
	String getName();

}
