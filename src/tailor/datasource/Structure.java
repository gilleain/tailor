package tailor.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import tailor.Level;
import tailor.geometry.Vector;

public class Structure implements Iterable<Structure> {
	
	private ArrayList<Structure> children;
	private HashMap<String, String> properties;
	private Vector center;
	private Level level;
	
	public Structure() {
		this.children = new ArrayList<Structure>();
		this.properties = new HashMap<String, String>();
		this.center = null;
		this.level = Level.UNKNOWN;
	}
	
	public Structure(Level level) {
		this();
		this.level = level;
	}
	
	@Override
    public Iterator<Structure> iterator() {
        return children.iterator();
    }

    public Level getLevel() {
		return level;
	}

	public void addSubStructure(Structure structure) {
		this.children.add(structure);
	}
    
    public int size() {
        return this.children.size();
    }
	
	/**
	 * Return a reference to the children.
	 * 
	 * @return an ArrayList of child Structures
	 */
	public ArrayList<Structure> getSubStructures() {
		return this.children;
	}
    
    public Structure getSubStructureAtIndex(int index) {
        return this.children.get(index);
    }
    
    public Structure getLastSubStructure() {
        return this.children.get(this.size() - 1);
    }
	
	public Structure getSubStructureByProperty(String propertyName, String propertyValue) {
		for (Structure structure : this.children) {
			if (structure.getProperty(propertyName).equals(propertyValue)) {
				return structure;
			}
		}
		return null;
	}
	


    /**
	 * This is probably just the clone method...
	 * 
	 * @return a shallow copy, with no children but with a copy of the properties Map.
	 */
	public Structure shallowCopy() {
		Structure copy =  new Structure(this.level);
		try {
			copy.properties = (HashMap<String, String>) this.properties.clone();
		} catch(ClassCastException cce) {
			// TODO : what?
		}
//        System.err.println(copy.properties);
		return copy;
	}
	

	
	/**
	 * Recursively determine the center, or return a center property
	 * if there are no children.
	 * 
	 * @return a Vector that is the center of this feature
	 */
	public Vector getCenter() {
	    System.out.println("getting center for " + this);
		if (this.children.size() > 0) {
			Vector total = new Vector(0.0, 0.0, 0.0);
			for (Structure subFeature : this.children) {
				total = total.plus(subFeature.getCenter());
			}
			return total.divide(this.children.size());
		} else {
		    return this.getAtomCenter();
		}
	}
    
    public Vector getAtomCenter() {
        if (this.center == null) {
            String coords = this.getProperty("Coords");
            this.center = new Vector(coords);
        }
        return this.center;
    }
	
	/**
	 * This method treats the Structure as a wrapper for a HashMap.
	 * 
	 * @param propertyKey the key to lookup
	 * @param propertyValue the value for the key
	 * @return true only if the Structure has a property with this name
	 *  and the correct value.
	 */
	public boolean hasPropertyEqualTo(String propertyKey, String propertyValue) {
		return this.properties.containsKey(propertyKey)
			&& this.properties.get(propertyKey).equals(propertyValue);
	}

	public String getProperty(String propertyName) {
		return this.properties.get(propertyName);
	}
	
	public void setProperty(String propertyName, String propertyValue) {
		this.properties.put(propertyName, propertyValue);
	}
    
    public String getId() {
        switch (this.level) {
            case PROTEIN : return this.getProperty("Name");
            case CHAIN : return this.getProperty("Name");
            case RESIDUE : return this.getProperty("Number");
            case ATOM : return this.getProperty("Name");
            default : return "";
        }
    }
	
	public String toString() {
		StringBuffer s = new StringBuffer();
        if (this.level == Level.PROTEIN) {
            s.append(this.getId()).append(".");
            for (Structure chain : this.children) {
                s.append(chain.toString()).append(' ');
            }
        }
        
        if (this.level == Level.CHAIN) {
            s.append(this.getId()).append(' ');
            Structure f = this.children.get(0);
            Structure l = this.children.get(this.size() - 1);
            s.append(f.getProperty("Name")).append(f.getId()).append("-");
            s.append(l.getProperty("Name")).append(l.getId());
        }
        
		return s.toString();
	}
	
}
