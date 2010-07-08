package tailor.description;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tailor.Level;
import tailor.condition.Condition;
import tailor.condition.TorsionBoundCondition;
import tailor.datasource.Structure;


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
    
    public GroupDescription() {
        this.groupName = null;
        this.atomDescriptions = new ArrayList<AtomDescription>();
        this.atomConditions = new ArrayList<Condition>();
    }
    
    public GroupDescription(String groupName) {
        this();
        this.groupName = groupName;
    }
    
    public GroupDescription(GroupDescription groupDescription) {
    	this(groupDescription.groupName);
    	for (AtomDescription atomDescription : groupDescription.atomDescriptions) {
    		this.atomDescriptions.add(new AtomDescription(atomDescription));
    	}
    	// TODO : atom conditions? what even are atom conditions?
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
        return new GroupDescription(this.groupName);
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
    
    public Description getPathEnd() {
    	if (this.atomDescriptions.size() == 0) {
    		return this;
    	} else {
    		return this.atomDescriptions.get(0).getPathEnd();
    	}
    }
    
    public String toPathString() {
        StringBuffer s = new StringBuffer();
        s.append("i + ").append(0).append("/"); // TODO
        for (AtomDescription atomDescription : this.atomDescriptions) {
            s.append(atomDescription.getName());
        }
        return s.toString();
    }
    
    public String toXmlPathString() {
    	// XXX we assume that there is only one Atom!
    	String s = "<Path position=\"" + 0 + "\" ";    // TODO 
    	return s + this.atomDescriptions.get(0).toXmlPathString();
    }
    
    public String toString() {
        return "Group " 
            + ((this.groupName == null)? "" : this.groupName)
            + 0;    // TODO
    }

}
