package tops.translation.experimental;

import tailor.api.Measurement;
import tailor.api.SegmentIntPropertyMeasure;
import tailor.measurement.SimpleMeasurement;
import tops.translation.model.Segment;

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
