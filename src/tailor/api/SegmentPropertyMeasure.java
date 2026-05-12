package tailor.api;

import tops.translation.experimental.SegmentDescription;
import tops.translation.model.BackboneSegment;

/**
 * Measurement to make on a single segment.
 */
public interface SegmentPropertyMeasure {
	
	Measurement measure(BackboneSegment segment);

	/**
	 * @return the segment description the measure applies to
	 */
	SegmentDescription getSegmentDescription();

	/**
	 * @return a descriptive name for the measure
	 */
	String getName();

}
