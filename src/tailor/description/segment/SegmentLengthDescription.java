package tailor.description.segment;

import tailor.api.SegmentIntPropertyMeasure;
import tailor.api.SegmentPropertyDescription;
import tailor.condition.IntegerLowerBoundCondition;
import tailor.measure.SegmentLengthMeasure;
import tailor.structure.Segment;

public class SegmentLengthDescription implements SegmentPropertyDescription {
	
	private final SegmentLengthMeasure segmentLengthMeasurement;
	
	private final IntegerLowerBoundCondition condition;

	public SegmentLengthDescription(int value, SegmentDescriptionPath segmentDescriptionPath) {
		this.condition = new IntegerLowerBoundCondition(value);
		this.segmentLengthMeasurement = new SegmentLengthMeasure(segmentDescriptionPath);
	}

	@Override
	public SegmentDescription getSegmentDescription() {
		return segmentLengthMeasurement.getSegmentDescription();
	}

	@Override
	public SegmentIntPropertyMeasure createMeasure() {
		return segmentLengthMeasurement;
	}

	@Override
	public boolean apply(Segment segment) {
		return this.segmentLengthMeasurement.measure(segment).apply(this.condition);
	}

}
