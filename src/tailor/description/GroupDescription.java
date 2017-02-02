package tailor.description;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tailor.condition.Condition;
import tailor.condition.TorsionBoundCondition;
import tailor.datasource.Structure;
import tailor.measurement.Measure;
import tailor.structure.Level;


/**
 * Describes a group of atoms, such as a residue in a protein.
 * 
 * @author maclean
 *
 */
public class GroupDescription implements Description, Iterable<AtomDescription> {
    
    private static final Level level = Level.RESIDUE;
    
    private String groupName;
    
    private ArrayList<AtomDescription> atomDescriptions;
    
    private ArrayList<Condition> atomConditions;
    
    private List<Measure> atomMeasures;
    
    private Map<Integer, Description> descriptionLookup;
    
    private int id;
    
    /**
     * A label for a group is some identifier that makes sense in the context of
     * the pattern, like "i + 1" although note that is just intended as an identifier
     * and not be parsable as a number
     */
    private String label;
    
    public GroupDescription() {
        this.groupName = null;
        this.atomDescriptions = new ArrayList<AtomDescription>();
        this.atomMeasures = new ArrayList<Measure>();
        this.atomConditions = new ArrayList<Condition>();
        this.descriptionLookup = new HashMap<Integer, Description>();
    }
    
    public GroupDescription(String groupName) {
        this();
        this.groupName = groupName;
    }
    
    // XXX multiple constructors - bleh
    public GroupDescription(String groupName, String label) {
        this();
        this.groupName = groupName;
        this.label = label;
    }
    
    public GroupDescription(GroupDescription groupDescription) {
    	this(groupDescription.groupName);
    	for (AtomDescription atomDescription : groupDescription.atomDescriptions) {
    		this.atomDescriptions.add(new AtomDescription(atomDescription));
    	}
    	// TODO : atom conditions? what even are atom conditions?
    }
    
    public void addAtomDescription(AtomDescription atomDescription) {
        this.atomDescriptions.add(atomDescription);
        int id = this.id + atomDescriptions.size();
//        atomDescription.setID(id);
        this.descriptionLookup.put(id, atomDescription);
    }
    
    public void addAtomDescription(String atomName) {
    	this.addAtomDescription(new AtomDescription(atomName));
    }
    
    public void addAtomCondition(Condition condition) {
        this.atomConditions.add(condition);
    }
    
    public ArrayList<AtomDescription> getAtomDescriptions() {
        return this.atomDescriptions;
    }
    
    public List<TorsionBoundCondition> getTorsionBoundConditions() {
        ArrayList<TorsionBoundCondition> torsions = 
            new ArrayList<TorsionBoundCondition>();
        for (Condition condition : this.atomConditions) {
            if (condition instanceof TorsionBoundCondition) {
                torsions.add((TorsionBoundCondition) condition);
            }
        }
        
        return torsions;
    }
    
    /**
     * The basic match : consider only the group name
     * but not any attached conditions.
     * 
     * @param residue the Structure to compare to
     * @return true if this has no set name or the names match
     */
    public boolean nameMatches(Structure residue) {
        return this.groupName == null 
            || this.groupName.equals(residue.getProperty("Name"));
    }

    public boolean nameMatches(String groupName) {
        return this.groupName.equals(groupName);
    }

    public AtomDescription getAtomDescription(String atomName) {
        // TODO : what if multiple matches?
        
        for (AtomDescription atomDescription : this.atomDescriptions) {
            if (atomDescription.nameMatches(atomName)) {
                
                // XXX we could return a new object, but why bother?
                return atomDescription;
            }
        }
        
        // TODO : what if no matches?
        System.err.println("no atom with name " + atomName + " in " + this);
        return null;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(int id) {
        this.id = id;
    }

    public Description getByID(int id) {
        if (descriptionLookup.containsKey(id)) {
            return descriptionLookup.get(id);
        } else {
            for (AtomDescription atomD : this) {
                Description d = atomD.getByID(id);
                if (d != null) {
                    return d;
                }
            }
        }
        return null;
    }

    public Iterator<AtomDescription> iterator() {
        return atomDescriptions.iterator();
    }

    public Object clone() {
    	return new GroupDescription(this);
    }

    public boolean contains(Description d) {
        	if (d.getLevel() == GroupDescription.level) {
        	    // TODO
    //    		return this.getOffset() == ((GroupDescription) d).getOffset();
        	    return true;
        	} else {
        		for (AtomDescription atom : this.atomDescriptions) {
        			if (atom.getName().equals(((AtomDescription) d).getName())) {
        				return true;
        			}
        		}
        		return false;
        	}
        }

    public Description shallowCopy() {
        // XXX TODO UGH - this is now horrible
        if (this.label == null) {
            return new GroupDescription(this.groupName);
        } else {
            return new GroupDescription(this.groupName, this.label);
        }
    }

    public Level getLevel() {
        return GroupDescription.level;
    }

    public void addSubDescription(Description subDescription) {
        if (subDescription instanceof AtomDescription) {
            this.addAtomDescription((AtomDescription) subDescription);
        } else {
            // TODO : type safety
        }
    }

    public void addCondition(Condition condition) {
        this.atomConditions.add(condition);
    }

    public ArrayList<Condition> getConditions() {
        return this.atomConditions;
    }

    public void addMeasure(Measure measure) {
        atomMeasures.add(measure);
    }

    public List<Measure> getMeasures() {
        return this.atomMeasures;
    }

    public int size() {
        return this.atomDescriptions.size();
    }

    public ArrayList<AtomDescription> getSubDescriptions() {
        return this.atomDescriptions;
    }

    public Description getSubDescriptionAt(int i) {
        return atomDescriptions.get(i);
    }

    public String getName() {
    	return this.groupName;
    }
    
    public Description getPathEnd() {
    	if (this.atomDescriptions.size() == 0) {
    		return this;
    	} else {
    		return this.atomDescriptions.get(0).getPathEnd();
    	}
    }
    
    public String toPathString() {
        StringBuffer s = new StringBuffer();
        s.append(label).append("/"); // TODO
        for (AtomDescription atomDescription : this.atomDescriptions) {
            s.append(atomDescription.getName());
        }
        return s.toString();
    }
    
    public String toXmlPathString() {
        String s;
        if (label != null) {
            s = "<Path =\"" + label + "\" "; 
        } else {
            s = "<Path ";   // TODO ?
        }
    	 // XXX we assume that there is only one Atom!
    	return s + this.atomDescriptions.get(0).toXmlPathString();
    }
    
    public String toString() {
        return "Group " 
            + ((this.groupName == null)? "" : this.groupName)
            + 0;    // TODO
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }

    public boolean labelMatches(String groupLabel) {
        return groupLabel.equals(label);
    }

}
