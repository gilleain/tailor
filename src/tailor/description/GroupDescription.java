package tailor.description;

import java.util.ArrayList;

import tailor.Level;
import tailor.condition.Condition;
import tailor.condition.TorsionBoundCondition;
import tailor.datasource.Structure;
import tailor.geometry.Vector;


/**
 * @author maclean
 *
 */
public class GroupDescription implements Description {
    
    private static final Level level = Level.RESIDUE;
    
    private String groupName;
    
    private int offset;
    
    private ArrayList<AtomDescription> atomDescriptions;
    
    private ArrayList<Condition> atomConditions;
    
    public GroupDescription() {
        this.groupName = null;
        this.offset = -1; // NOTE : -1 means 'not set'
        this.atomDescriptions = new ArrayList<AtomDescription>();
        this.atomConditions = new ArrayList<Condition>();
    }
    
    public GroupDescription(String groupName, int offset) {
        this();
        this.groupName = groupName;
        this.offset = offset;
    }
    
    public GroupDescription(GroupDescription groupDescription) {
    	this(groupDescription.groupName, groupDescription.offset);
    	for (AtomDescription atomDescription : groupDescription.atomDescriptions) {
    		this.atomDescriptions.add(new AtomDescription(atomDescription));
    	}
    	// TODO : atom conditions? what even are atom conditions?
    }
    
    public Object clone() {
    	return new GroupDescription(this);
    }
    
    public boolean contains(Description d) {
    	if (d.getLevel() == GroupDescription.level) {
    		return this.getOffset() == ((GroupDescription) d).getOffset();
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
        return new GroupDescription(this.groupName, this.offset);
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
    
    public int size() {
        return this.atomDescriptions.size();
    }
    
    public void addAtomDescription(AtomDescription atomDescription) {
        this.atomDescriptions.add(atomDescription);
    }
    
    public void addAtomDescription(String atomName) {
    	this.addAtomDescription(new AtomDescription(atomName));
    }
    
    public void addAtomCondition(Condition condition) {
        this.atomConditions.add(condition);
    }
    
    public ArrayList<AtomDescription> getSubDescriptions() {
        return this.atomDescriptions;
    }
    
    public ArrayList<AtomDescription> getAtomDescriptions() {
        return this.atomDescriptions;
    }
    
    public boolean conditionsSatisfied(Structure group) {
        for (Condition condition : this.atomConditions) {
            if (condition.satisfiedBy(group)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }
    
    public ArrayList<TorsionBoundCondition> getTorsionBoundConditions() {
        ArrayList<TorsionBoundCondition> torsions = new ArrayList<TorsionBoundCondition>();
        for (Condition condition : this.atomConditions) {
            if (condition instanceof TorsionBoundCondition) {
                torsions.add((TorsionBoundCondition) condition);
            }
        }
        
        return torsions;
    }
    
    /**
     * Try to find in Structure <code>group</code>
     * all the atoms that match the AtomDescriptions
     * in this GroupDescription.
     * 
     * @param group
     * @return a Structure
     */
    public Structure matchTo(Structure group) {
        Structure matchingGroup = new Structure(Level.RESIDUE);
        for (AtomDescription atomDescription : this.atomDescriptions) {
            boolean matchFound = false;
            for (Structure atom : group.getSubStructures()) {
                if (atomDescription.matches(atom)) {
                    matchingGroup.addSubStructure(atom);
                    matchFound = true;
                    break;
                }
            }
            
            // purely an optimization step...
            if (!matchFound) {
                return matchingGroup;   // an incomplete one
            }
        }
        return matchingGroup;
    }
    
    /**
     * Search the supplied structure to find the
     * subtree matching this description and
     * return the center of mass (or atom coord).
     * 
     * @param group the (fragment of a) group to search
     * @return a Vector
     */
    public Vector findStructureCenter(Structure group) {
        Vector center = new Vector();
        
        for (AtomDescription atomDescription : this.atomDescriptions) {
            for (Structure atom : group.getSubStructures()) {
                if (atomDescription.matches(atom)) {
                    center.add(atomDescription.findStructureCenter(atom));
                    break;  // only one atom should match a description?
                }
            }
        }
        
        return center.divide(this.size());
    }
    
    public String getName() {
    	return this.groupName;
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
    
    /**
     * The basic match : consider only the group name
     * but not any attached conditions.
     * 
     * @param residue the Structure to compare to
     * @return true if this has no set name or the names match
     */
    public boolean matches(Structure residue) {
        return this.groupName == null || this.groupName.equals(residue.getProperty("Name"));
    }
    
    public int getOffset() {
        return this.offset;
    }
    
    public boolean offsetMatches(int offset) {
        return this.offset == offset;
    }
    
    public boolean nameMatches(String groupName) {
        return this.groupName.equals(groupName);
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
        s.append("i + ").append(this.offset).append("/");
        for (AtomDescription atomDescription : this.atomDescriptions) {
            s.append(atomDescription.getName());
        }
        return s.toString();
    }
    
    public String toXmlPathString() {
    	// XXX we assume that there is only one Atom!
    	String s = "<Path position=\"" + this.offset + "\" "; 
    	return s + this.atomDescriptions.get(0).toXmlPathString();
    }
    
    public String toString() {
        return "Group " 
            + ((this.groupName == null)? "" : this.groupName)
            + this.offset;
    }

}
