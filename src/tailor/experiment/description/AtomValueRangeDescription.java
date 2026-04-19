package tailor.experiment.description;

import java.util.List;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.api.AtomListDescription;
import tailor.experiment.api.AtomListMeasure;
import tailor.experiment.condition.AtomRangeCondition;

public class AtomValueRangeDescription implements AtomListDescription  {
	
	private final double minValue;
	
	private final double maxValue;
	
	private final AtomListMeasure measure;
	
	public AtomValueRangeDescription(double minValue, double maxValue, AtomListMeasure measure) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.measure = measure;
	}

	@Override
	public AtomListCondition createCondition() {
		return new AtomRangeCondition(this.minValue, this.maxValue, this.measure);
	}

	@Override
	public AtomListMeasure createMeasure() {
		return this.measure;
	}

	@Override
	public List<GroupDescription> getGroupDescriptions() {
		return this.measure.getGroupDescriptions();
	}

}
