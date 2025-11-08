package aigen.description;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import aigen.feature.Feature;

public abstract class Description implements Iterable<Description> {
	
	 protected List<Description> children;
	 protected List<Object> conditions; // TODO Would be Condition interface in full implementation
	 protected Map<String, Object> properties;
	
	
    public Description(Map<String, Object> propertyConditions) {
	    this.children = new ArrayList<>();
	    this.conditions = new ArrayList<>();
	    this.properties = new HashMap<>();
	    addPropertyConditions(propertyConditions);
	}
    
	public abstract String getLevelCode();
    public abstract String getName();
    public abstract int length();
    public abstract boolean describes(Feature feature);
    public abstract Class<? extends Feature> getFeatureType();
    
    public List<Object> getConditions() {
    	return this.conditions;
    }
    
    protected void addPropertyConditions(Map<String, Object> propertyConditions) {
        if (propertyConditions != null) {
            for (Map.Entry<String, Object> entry : propertyConditions.entrySet()) {
                addPropertyCondition(entry.getKey(), entry.getValue());
            }
        }
    }
    
    public void addPropertyCondition(String propertyName, Object propertyValue) {
        properties.put(propertyName, propertyValue);
        // conditions.add(new PropertyCondition(propertyName, propertyValue));
    }
    
    public void addCondition(Object condition) {
        conditions.add(condition);
    }
    
    public boolean describes(Object feature) {
        for (Object condition : conditions) {
            // if (!condition.satisfiedBy(feature)) {
            //     return false;
            // }
        }
        return matchesSubFeatures(feature);
    }
    
    protected boolean matchesSubFeatures(Object feature) {
        for (Description subDescription : children) {
            boolean foundMatch = false;
            // Iterate through subfeatures of feature
            // This would need actual feature implementation
            if (!foundMatch) {
                return false;
            }
        }
        return true;
    }
    
    public void addSubDescription(Description subDescription) {
        children.add(subDescription);
    }
    
    public boolean hasSubDescriptions() {
        return children.size() > 0;
    }
    
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }
    
    public int size() {
        return children.size();
    }
    
    public Iterator<Description> iterator() {
        return children.iterator();
    }
    
    public String toStr() {
        return toString() + " " + children.toString();
    }

	public List<Description> getChildren() {
		return this.children;
	}

	public void setName(String string) {
		// TODO Auto-generated method stub
		
	}
}

