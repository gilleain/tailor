package tailor.description.atom;

import java.util.List;

import tailor.api.AtomListDescription;
import tailor.api.AtomListMeasure;
import tailor.api.Condition;
import tailor.api.Measurement;
import tailor.condition.AtomPartition;

public class AtomValueDescription implements AtomListDescription {
	
	private final Condition<Double> condition;
	
	private final AtomListMeasure measure;
	
	public AtomValueDescription(Condition<Double> condition, AtomListMeasure measure) {
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
		Measurement<Double> m = measure.measure(atomPartition);
		return m.apply(condition);
	}

}
