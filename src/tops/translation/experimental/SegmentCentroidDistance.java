package tops.translation.experimental;

public class SegmentCentroidDistance extends SegmentUpperBound {

	public SegmentCentroidDistance(double value, DescriptionPath pathA, DescriptionPath pathB) {
		super(value, new SegmentCentroidDistanceMeasure(pathA, pathB));
	}

}
