package tailor.api;

import tailor.structure.Segment;
import tops.translation.experimental.SegmentDescription;

/**
 * Description for a segments that can create conditions and matchers for filtering.
 */
public interface SegmentPropertyDescription {

	/**
	 * @return the segment description referenced by this description
	 */
	SegmentDescription getSegmentDescription();
	
	/**
	 * @return convert this description to a measure across the same segment
	 */
	SegmentIntPropertyMeasure createMeasure();
	
	/**
	 * 
	 * @return the result of applying this description to the segment
	 */
	boolean apply(Segment segment);

}
