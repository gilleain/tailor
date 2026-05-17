package tailor.measure;

import tailor.api.Measurement;
import tailor.api.SegmentIntPropertyMeasure;
import tailor.description.segment.SegmentDescription;
import tailor.description.segment.SegmentDescriptionPath;
import tailor.measurement.SimpleMeasurement;
import tailor.structure.Segment;

public class SegmentLengthMeasure implements SegmentIntPropertyMeasure {
	
	private final SegmentDescriptionPath path;

	public SegmentLengthMeasure(SegmentDescriptionPath path) {
		this.path = path;
	}

	@Override
	public Measurement<Integer> measure(Segment segment) {
		return new SimpleMeasurement<Integer>(segment.length());
	}

	@Override
	public SegmentDescription getSegmentDescription() {
		return path.getSegmentDescription();
	}

	@Override
	public String getName() {
		return "Segment length";
	}

}
