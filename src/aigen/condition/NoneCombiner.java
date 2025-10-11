package aigen.condition;

import java.util.ArrayList;
import java.util.List;

class NoneCombiner extends BaseCondition {
	private List<Condition> conditions;

	public NoneCombiner() {
		this.conditions = new ArrayList<>();
	}

	public NoneCombiner(List<Condition> conditions) {
		this.conditions = conditions != null ? conditions : new ArrayList<>();
	}

	@Override
	public boolean satisfiedBy(Object feature) {
		int numberSatisfied = 0;
		for (Condition condition : conditions) {
			if (condition.satisfiedBy(feature)) {
				numberSatisfied++;
			}
		}
		return numberSatisfied == 0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("None of [");
		for (int i = 0; i < conditions.size(); i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(conditions.get(i).toString());
		}
		sb.append("]");
		return sb.toString();
	}
}
