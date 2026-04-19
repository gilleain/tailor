package tailor.experiment.description;

import java.util.List;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.api.AtomListDescription;
import tailor.experiment.api.AtomListMeasure;
import tailor.experiment.condition.AtomUpperBoundCondition;

public class AtomUpperBoundDescription implements AtomListDescription {
	
	private final double value;
	
	private final AtomListMeasure measure;
	
	public AtomUpperBoundDescription(double value, AtomListMeasure measure) {
		this.value = value;
		this.measure = measure;
	}
	

	@Override
	public AtomListCondition createCondition() {
		return new AtomUpperBoundCondition(this.value, this.measure);
	}
	
	@Override
	public AtomListMeasure createMeasure() {
		return this.measure;
	}


	@Override
	public List<GroupDescription> getGroupDescriptions() {
		return measure.getGroupDescriptions();
	}

}
