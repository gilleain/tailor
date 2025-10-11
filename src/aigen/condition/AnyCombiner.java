package aigen.condition;

import java.util.ArrayList;
import java.util.List;

class AnyCombiner extends BaseCondition {
	private List<Condition> conditions;

	public AnyCombiner() {
		this.conditions = new ArrayList<>();
	}

	public AnyCombiner(List<Condition> conditions) {
		this.conditions = conditions != null ? conditions : new ArrayList<>();
	}

	@Override
	public boolean satisfiedBy(Object feature) {
		for (Condition condition : conditions) {
			if (condition.satisfiedBy(feature)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Any of [");
		for (int i = 0; i < conditions.size(); i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(conditions.get(i).toString());
		}
		sb.append("]");
		return sb.toString();
	}
}
