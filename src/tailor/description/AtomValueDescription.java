package tailor.description;

import java.util.List;

import tailor.api.AtomListDescription;
import tailor.api.AtomListMeasure;
import tailor.api.Measurement;
import tailor.api.MeasurementCondition;
import tailor.condition.AtomPartition;

public class AtomValueDescription implements AtomListDescription {
	
	private final MeasurementCondition condition;
	
	private final AtomListMeasure measure;
	
	public AtomValueDescription(MeasurementCondition condition, AtomListMeasure measure) {
		this.condition = condition;
		this.measure = measure;
	}

	@Override
	public AtomListMeasure createMeasure() {
		return this.measure;
	}

	@Override
	public List<GroupDescription> getGroupDescriptions() {
		return measure.getGroupDescriptions();
	}
	
	public boolean apply(AtomPartition atomPartition) {
		Measurement m = measure.measure(atomPartition);
		return condition.accept(m);
	}

}
