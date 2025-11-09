package tailor.condition;

import java.util.ArrayList;
import java.util.List;

import tailor.description.Description;
import tailor.match.Match;

/**
 * @author maclean
 *
 */
public class All extends BaseCondition implements Condition {

	private List<Condition> conditions;

	public All() {
		this.conditions = new ArrayList<>();
	}

	/**
	 * @param conditions
	 */
	public All(List<Condition> conditions) {
		this.conditions = conditions != null ? conditions : new ArrayList<>();
	}
	
	public boolean satisfiedBy(Match match) {
		for (Condition condition : this.conditions) {
			if (condition.satisfiedBy(match)) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	public boolean contains(Description d) {
		// TODO
		return false;
	}

	public Object clone() {
		// TODO
		return null;
	}

	public int hashCode() {
		int hashcode = 1;
		for (Condition condition : conditions) {
			hashcode *= condition.hashCode();
		}
		return hashcode;
	}

	public boolean equals(Object other) {
		return other instanceof All; // TODO - check the conditions in this
	}


	public String toXml() {
		return ""; // TODO
	}

	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("All [ ");
		for (Condition condition : this.conditions) {
			stringBuffer.append(condition.toString()).append(", ");
		}
		stringBuffer.append("]");
		return stringBuffer.toString();
	}

}
