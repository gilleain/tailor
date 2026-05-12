package tops.translation.experimental;

import tailor.api.SegmentPropertyDescription;
import tailor.api.SegmentPropertyMeasure;
import tailor.condition.LowerBoundCondition;
import tops.translation.model.BackboneSegment;

public class SegmentLength implements SegmentPropertyDescription {
	
	private final SegmentLengthMeasurement segmentLengthMeasurement;
	
	private final LowerBoundCondition condition;

	public SegmentLength(int value, SegmentDescriptionPath segmentDescriptionPath) {
		this.condition = new LowerBoundCondition(value);
		this.segmentLengthMeasurement = new SegmentLengthMeasurement(segmentDescriptionPath);
	}

	@Override
	public SegmentDescription getSegmentDescription() {
		return segmentLengthMeasurement.getSegmentDescription();
	}

	@Override
	public SegmentPropertyMeasure createMeasure() {
		return segmentLengthMeasurement;
	}

	@Override
	public boolean apply(BackboneSegment segment) {
		return this.condition.accept(this.segmentLengthMeasurement.measure(segment));
	}

}
