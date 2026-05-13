package tailor.description;

import java.util.List;

import tailor.api.AtomListDescription;
import tailor.api.AtomListMeasure;
import tailor.api.Measurement;
import tailor.condition.AtomPartition;
import tailor.condition.RangeCondition;

public class AtomValueRangeDescription implements AtomListDescription  {
	
	private String name;
	
	private final RangeCondition condition;
	
	private final AtomListMeasure measure;
	
	public AtomValueRangeDescription(double minValue, double maxValue, AtomListMeasure measure) {
		this("", minValue, maxValue, measure);
	}
	
	public AtomValueRangeDescription(String name, double minValue, double maxValue, AtomListMeasure measure) {
		this.condition = new RangeCondition(minValue, maxValue);
		this.measure = measure;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public AtomListMeasure createMeasure() {
		return this.measure;
	}

	@Override
	public List<GroupDescription> getGroupDescriptions() {
		return this.measure.getGroupDescriptions();
	}

	public double getMinValue() {
		return condition.getMinValue();
	}

	public double getMaxValue() {
		return condition.getMaxValue();
	}
	
	public boolean apply(AtomPartition atomPartition) {
		Measurement<Double> m = measure.measure(atomPartition);
		return m.apply(condition);
	}
}
