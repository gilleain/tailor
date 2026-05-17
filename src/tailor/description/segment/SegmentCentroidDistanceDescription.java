package tailor.description.segment;

import tailor.measure.SegmentCentroidDistanceMeasure;

public class SegmentCentroidDistanceDescription extends SegmentUpperBound {

	public SegmentCentroidDistanceDescription(double value, SegmentDescriptionPath pathA, SegmentDescriptionPath pathB) {
		super(value, new SegmentCentroidDistanceMeasure(pathA, pathB));
	}

}
