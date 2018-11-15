package tailor.condition;

import java.util.ArrayList;

import tailor.description.Description;
import tailor.match.Match;


public class Any implements Condition {
    
    private ArrayList<Condition> conditions;

    /**
     * @param conditions
     */
    public Any(ArrayList<Condition> conditions) {
        this.conditions = conditions;
    }
    
    public boolean contains(Description d) {
    	// TODO
    	return false;
    }

    public Object clone() {
    	return null;	// TODO
    }
    
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof Any) {
            return true;    // TODO - check subconditions
        }
        return false;
    }

    public boolean satisfiedBy(Match match) {
        for (Condition condition : this.conditions) {
            if (condition.satisfiedBy(match)) {
                return true;
            } 
        }
        
        // if we haven't already returned, then none were true
        return false;
    }
    
    public String toXml() {
    	return "";	//TODO
    }
    
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Any [ ");
        for (Condition condition : this.conditions) {
            stringBuffer.append(condition.toString()).append(", ");
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }
}
