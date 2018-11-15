package tailor.condition;

import java.util.ArrayList;

import tailor.description.Description;
import tailor.match.Match;

/**
 * @author maclean
 *
 */
public class All implements Condition {
    
    private ArrayList<Condition> conditions;
    
    /**
     * @param conditions
     */
    public All(ArrayList<Condition> conditions) {
        this.conditions = conditions;
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
        return other instanceof All;    // TODO - check the conditions in this
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
    
    public String toXml() {
    	return "";	//TODO
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
