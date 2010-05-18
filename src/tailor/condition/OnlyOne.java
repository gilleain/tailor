package tailor.condition;

import java.util.ArrayList;

import tailor.datasource.Structure;
import tailor.description.Description;


public class OnlyOne implements Condition {
    
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
    
    public boolean equals(Condition other) {
        return false;
    }

    public boolean satisfiedBy(Structure structure) {
        boolean satisfied = false;
        for (Condition condition : this.conditions) {
            if (condition.satisfiedBy(structure)) {
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
