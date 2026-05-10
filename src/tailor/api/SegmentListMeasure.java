package tailor.api;

import java.util.List;

import tailor.condition.SegmentPartition;
import tops.translation.experimental.SegmentDescription;

/**
 * Measurement to make on a list of atoms.
 */
public interface SegmentListMeasure {
	
	Measurement measure(SegmentPartition segmentPartition);

	/**
	 * @return the segment descriptions the measure applies to
	 */
	List<SegmentDescription> getSegmentDescriptions();

	/**
	 * @return a descriptive name for the measure
	 */
	String getName();

}
