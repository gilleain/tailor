package tops.translation.experimental;

import java.util.List;

import tailor.api.Measurement;
import tailor.api.SegmentListMeasure;
import tailor.condition.SegmentPartition;

public class SegmentCentroidDistanceMeasure implements SegmentListMeasure {
	
	private final DescriptionPath pathA;
	private final DescriptionPath pathB;

	public SegmentCentroidDistanceMeasure(DescriptionPath pathA, DescriptionPath pathB) {
		this.pathA = pathA;
		this.pathB = pathB;
	}

	@Override
	public Measurement measure(SegmentPartition segmentPartition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SegmentDescription> getSegmentDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "Segment centroid distance";
	}

}
