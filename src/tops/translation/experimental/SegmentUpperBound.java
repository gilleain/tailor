package tops.translation.experimental;

import java.util.List;

import tailor.api.Measurement;
import tailor.api.SegmentListDescription;
import tailor.api.SegmentListMeasure;
import tailor.condition.SegmentPartition;
import tailor.condition.UpperBoundCondition;

public class SegmentUpperBound implements SegmentListDescription {
	
	private final UpperBoundCondition condition;
	
	private final SegmentListMeasure measure;
	
	public SegmentUpperBound(double value, SegmentListMeasure measure) {
		this.condition = new UpperBoundCondition(value);
		this.measure = measure;
	}

	@Override
	public List<SegmentDescription> getSegmentDescriptions() {
		return this.measure.getSegmentDescriptions();
	}

	@Override
	public SegmentListMeasure createMeasure() {
		return this.measure;
	}

	@Override
	public boolean apply(SegmentPartition segmentPartition) {
		Measurement<Double> m = measure.measure(segmentPartition);
		return m.apply(condition);
	}

}
