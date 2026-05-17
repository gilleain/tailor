package tailor.api;

import java.util.List;

import tailor.condition.SegmentPartition;
import tailor.description.segment.SegmentDescription;

/**
 * Measurement to make on a list of segments.
 */
public interface SegmentListMeasure {
	
	Measurement<Double> measure(SegmentPartition segmentPartition);

	/**
	 * @return the segment descriptions the measure applies to
	 */
	List<SegmentDescription> getSegmentDescriptions();

	/**
	 * @return a descriptive name for the measure
	 */
	String getName();

}
