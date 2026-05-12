package tops.translation.experimental;

import tailor.api.Measurement;
import tailor.api.SegmentPropertyMeasure;
import tailor.measurement.DoubleMeasurement;
import tops.translation.model.BackboneSegment;

public class SegmentLengthMeasurement implements SegmentPropertyMeasure {
	
	private final SegmentDescriptionPath path;

	public SegmentLengthMeasurement(SegmentDescriptionPath path) {
		this.path = path;
	}

	@Override
	public Measurement measure(BackboneSegment segment) {
		return new DoubleMeasurement(segment.length());	// TODO - IntegerMeasurement ?
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
