package tailor.description;

import java.util.List;

import tailor.api.AtomListDescription;
import tailor.api.AtomListMeasure;
import tailor.api.Measurement;
import tailor.condition.AtomPartition;
import tailor.condition.UpperBoundCondition;

public class AtomUpperBoundDescription implements AtomListDescription {
	
	private final UpperBoundCondition condition;
	
	private final AtomListMeasure measure;
	
	public AtomUpperBoundDescription(double value, AtomListMeasure measure) {
		this.condition = new UpperBoundCondition(value);
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
