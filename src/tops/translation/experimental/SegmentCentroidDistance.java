package tops.translation.experimental;

public class SegmentCentroidDistance extends SegmentUpperBound {

	public SegmentCentroidDistance(double value, SegmentDescriptionPath pathA, SegmentDescriptionPath pathB) {
		super(value, new SegmentCentroidDistanceMeasure(pathA, pathB));
	}

}
