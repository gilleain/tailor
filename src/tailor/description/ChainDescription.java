package tailor.description;

import java.util.ArrayList;
import java.util.Iterator;

import tailor.Level;
import tailor.condition.Condition;
import tailor.datasource.Structure;
import tailor.geometry.Vector;


/**
 * @author maclean
 *
 */
public class ChainDescription implements Description, Iterable<GroupDescription> {
    
    private static final Level level = Level.CHAIN;
    
    private String chainName;
    
    private ArrayList<GroupDescription> groupDescriptions;
    
    private ArrayList<Condition> groupConditions;
    
    public ChainDescription() {
        this.chainName = null;
        this.groupDescriptions = new ArrayList<GroupDescription>();
        this.groupConditions = new ArrayList<Condition>();
    }
    
    public ChainDescription(String chainName) {
        this();
        this.chainName = chainName;
    }
    
    public ChainDescription(ChainDescription chainDescription) {
    	this(chainDescription.getName());
    	for (GroupDescription groupDescription : chainDescription.groupDescriptions) {
    		this.groupDescriptions.add(new GroupDescription(groupDescription));
    	}
    	for (Condition condition : chainDescription.getConditions()) {
    		this.groupConditions.add((Condition) condition.clone());
    	}
    }
    
    @Override
    public Iterator<GroupDescription> iterator() {
        return groupDescriptions.iterator();
    }

    public boolean contains(Description d) {
    	if (d.getLevel() == ChainDescription.level) {
    		return this.getName().equals(((ChainDescription) d).getName());
    	} else {
    		for (GroupDescription group : this.groupDescriptions) {
    			if (group.contains(d)) {
    				return true;
    			}
    		}
    		return false;
    	}
    }
    
    public Object clone() {
    	return new ChainDescription(this);
    }
    
    public Description shallowCopy() {
        return new ChainDescription(this.chainName);
    }
    
    public Level getLevel() {
        return ChainDescription.level;
    }
    
    public String getName() {
    	return this.chainName;
    }
    
    public boolean hasName(String name) {
    	return this.chainName.equals(name);
    }
    
    public void addCondition(Condition condition) {
        this.groupConditions.add(condition);
    }
    
    public ArrayList<Condition> getConditions() {
        return this.groupConditions;
    }
    
    public void addSubDescription(Description subDescription) {
        if (subDescription instanceof GroupDescription) {
            this.addGroupDescription((GroupDescription) subDescription);
        } else {
            // TODO : type checking - throw error
        }
    }
    
    public void addGroupDescription(GroupDescription groupDescription) {
        this.groupDescriptions.add(groupDescription);
    }
    
    public void removeLastGroupDescription() {
    	GroupDescription g = this.groupDescriptions.remove(this.groupDescriptions.size() - 1);
    	ArrayList<Condition> toRemove = new ArrayList<Condition>();
    	for (Condition condition : this.groupConditions) {
    		if (condition.contains(g)) {
    			toRemove.add(condition);
    		}
    	}
    	this.groupConditions.removeAll(toRemove);
    }
    
    public void addGroupCondition(Condition condition) {
        this.groupConditions.add(condition);
    }
    
    public ArrayList<GroupDescription> getSubDescriptions() {
        return this.groupDescriptions;
    }
    
    public ArrayList<GroupDescription> getGroupDescriptions() {
        return this.groupDescriptions;
    }
    
    /**
     * Get the group description at index <code>groupIndex</code>.
     * 
     * @param groupIndex
     * @return
     */
    public GroupDescription getGroupDescription(int groupIndex) {
        return groupDescriptions.get(groupIndex);
    }
    
    /**
     * @return the number of group descriptions in this chain
     */
    public int size() {
        return this.groupDescriptions.size();
    }
    
    public GroupDescription first() {
        return this.groupDescriptions.get(0);
    }
    
    public GroupDescription last() {
        return this.groupDescriptions.get(this.size() - 1);
    }
    
    public boolean conditionsSatisfied(Structure chain) {
        for (Condition condition : this.groupConditions) {
            if (condition.satisfiedBy(chain)) {
                continue;
            } else {
//                System.err.println(condition + " not satisfied by " + chain);
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * Search the supplied structure to find the
     * subtree matching this description and
     * return the center of mass (or atom coord).
     * 
     * @param chain the (fragment of a) chain to search
     * @return a Vector
     */
    public Vector findStructureCenter(Structure chain) {
        Vector center = new Vector();
        for (GroupDescription groupDescription : this.groupDescriptions) {
            for (Structure group : chain.getSubStructures()) {
                if (groupDescription.nameMatches(group)) {
//                    System.err.println("group matches : " + group.getId());
                    Vector groupCenter = 
                        groupDescription.findStructureCenter(group);
                    center.add(groupCenter);
                }
            }
        }
        
        return center.divide(this.size());
    }
    
    public ChainDescription getPath(String groupName, String atomName) {
        ChainDescription root = new ChainDescription(this.chainName);
        
        // TODO : what if multiple matches...
        for (GroupDescription groupDescription : this.groupDescriptions) {
            if (groupDescription.nameMatches(groupName)) {
                GroupDescription group = 
                    (GroupDescription) groupDescription.shallowCopy();
                AtomDescription atom = 
                    groupDescription.getAtomDescription(atomName);
                group.addAtomDescription(atom);
                root.addGroupDescription(group);
            }
        }
        
        return root;
    }
    
    public ChainDescription getPath(int groupIndex, String atomName) {
        ChainDescription root = new ChainDescription(this.chainName);
        
        // TODO : what if multiple matches...
        GroupDescription groupDescription = groupDescriptions.get(groupIndex);
        
        GroupDescription group = 
            (GroupDescription) groupDescription.shallowCopy();
        AtomDescription atom = new AtomDescription(atomName);
        group.addAtomDescription(atom);
        root.addGroupDescription(group);
        
        return root;
    }
    
    /**
     * The basic match : consider only the chain name and not
     * any attached conditions.
     * 
     * @param chain the Structure to compare to
     * @return true if this has no set chain name or the names are equal
     */
    public boolean nameMatches(Structure chain) {
        return this.chainName == null 
            || this.chainName.equals(chain.getProperty("Name"));
    }
    
    public Description getPathEnd() {
    	if (this.groupDescriptions.size() == 0) {
    		return this;
    	} else {
    		return this.groupDescriptions.get(0).getPathEnd();
    	}
    }
    
    public String toPathString() {
        StringBuffer s = new StringBuffer();
        s.append(this.chainName).append("/");
        for (GroupDescription groupDescription : this.groupDescriptions) {
            s.append(groupDescription.toPathString());
        }
        return s.toString();
    }
    
    public String toXmlPathString() {
    	// XXX we assume that there is only one Group!
    	return this.groupDescriptions.get(0).toXmlPathString();
    }
    
    public String toString() {
        return "Chain " + ((this.chainName == null)? "" : this.chainName);
    }

}
