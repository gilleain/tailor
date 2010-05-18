package tailor.condition;

import java.util.ArrayList;

import tailor.datasource.Structure;
import tailor.description.Description;


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
    
    public boolean equals(Condition other) {
        return false;
    }

    public boolean satisfiedBy(Structure structure) {
        for (Condition condition : this.conditions) {
            if (condition.satisfiedBy(structure)) {
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
