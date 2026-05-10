package tailor.api;

import java.util.List;

import tailor.condition.SegmentPartition;
import tops.translation.experimental.SegmentDescription;

/**
 * Description for a list of segments that can create conditions and matchers for filtering.
 */
public interface SegmentListDescription {

	/**
	 * @return all segment descriptions referenced by this description
	 */
	List<SegmentDescription> getSegmentDescriptions();
	
	/**
	 * @return convert this description to a measure across the same segments
	 */
	SegmentListMeasure createMeasure();
	
	/**
	 * 
	 * @return the result of applying this description to the segments
	 */
	boolean apply(SegmentPartition segmentPartition);

}
