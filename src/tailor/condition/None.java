package tailor.condition;

import java.util.ArrayList;

import tailor.datasource.Structure;
import tailor.description.Description;


public class None implements Condition {
    
    private ArrayList<Condition> conditions;

    /**
     * @param conditions
     */
    public None(ArrayList<Condition> conditions) {
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
        int numberSatisfied = 0;
        for (Condition condition : this.conditions) {
            if (condition.satisfiedBy(structure)) {
                numberSatisfied++;
            } else {
                continue;
            }
        }
        return numberSatisfied == 0;
    }
    
    public String toXml() {
    	return "";	//TODO
    }
    
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("None [ ");
        for (Condition condition : this.conditions) {
            stringBuffer.append(condition.toString()).append(", ");
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }
}
