package tailor.condition;

import tailor.description.Description;
import tailor.match.Match;


//TODO : this whole class may be unnecessary...
// TODO - how does this compare to BaseCondition.getProperty ...

public class PropertyCondition extends BaseCondition implements Condition {
	
	private String propertyKey;
	private String propertyValue;
	
	public PropertyCondition(String propertyKey, String propertyValue) {
		this.propertyKey = propertyKey;
		this.propertyValue = propertyValue;
	}
	
	public boolean contains(Description d) {
		// TODO
		return false;
	}

    public Object clone() {
    	return null;	// TODO
    }
    
    public int hashCode() {
        return propertyKey.hashCode() * propertyValue.hashCode();
    }
    
	public boolean equals(Object other) {
	    if (this == other) return true;
		if (other instanceof PropertyCondition) {
			PropertyCondition o = (PropertyCondition) other;
//            System.err.println(this + " == " + o);
			if (this.propertyKey.equals(o.propertyKey) && this.propertyValue.equals(o.propertyValue)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean keyEquals(String key) {
		return this.propertyKey.equals(key);
	}

	public boolean valueEquals(String value) {
		return this.propertyValue.equals(value);
	}
	
	public String getValue() {
		return this.propertyValue;
	}
	
	public boolean satisfiedBy(Match match) {
	    return false;  // XXX FIXME
//		return match.hasPropertyEqualTo(propertyKey, propertyValue);
	}
	
    public String toXml() {
    	return "";	//TODO
    }

	public String toString() {
		return String.format("%s : %s", this.propertyKey, this.propertyValue);
	}
}
