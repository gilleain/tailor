package tailor.api;

import tops.translation.experimental.SegmentDescription;
import tops.translation.model.BackboneSegment;

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
	SegmentPropertyMeasure createMeasure();
	
	/**
	 * 
	 * @return the result of applying this description to the segment
	 */
	boolean apply(BackboneSegment segment);

}
