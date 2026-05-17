package tailor.api;

import tailor.description.segment.SegmentDescription;
import tailor.structure.Segment;

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
