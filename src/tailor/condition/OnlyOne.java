package tailor.condition;

import java.util.ArrayList;

import tailor.description.Description;
import tailor.match.Match;


public class OnlyOne extends BaseCondition implements Condition {
    
    private ArrayList<Condition> conditions;

    /**
     * @param conditions
     */
    public OnlyOne(ArrayList<Condition> conditions) {
        this.conditions = conditions;
    }
    
    public boolean contains(Description d) {
    	// TODO
    	return false;
    }

    public Object clone() {
    	return null;	// TODO
    }
    
    public int hashCode() {
        int hashcode = 1;
        for (Condition condition : conditions) {
            hashcode *= condition.hashCode();
        }
        return hashcode;
    }
    
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof OnlyOne) {
            return true;    // TODO
        }
        return false;
    }

    public boolean satisfiedBy(Match match) {
        boolean satisfied = false;
        for (Condition condition : this.conditions) {
            if (condition.satisfiedBy(match)) {
                if (!satisfied) {
                    satisfied = true;
                    continue;
                } else {
                    return false;
                }
            } else {
                continue;
            }
        }
        return satisfied;
    }
    
    public String toXml() {
    	return "";	//TODO
    }
    
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("OnlyOne [ ");
        for (Condition condition : this.conditions) {
            stringBuffer.append(condition.toString()).append(", ");
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }

}
