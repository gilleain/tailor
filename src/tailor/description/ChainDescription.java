package tailor.description;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tailor.condition.Condition;
import tailor.datasource.Structure;
import tailor.measurement.Measure;
import tailor.structure.Level;


/**
 * @author maclean
 *
 */
public class ChainDescription implements Description, Iterable<GroupDescription> {
    
    private static final Level level = Level.CHAIN;
    
    private String chainName;
    
    private List<GroupDescription> groupDescriptions;
    
    private List<Condition> groupConditions;
    
    private List<Measure> groupMeasures;
    
    private Map<Integer, Description> descriptionLookup;
    
    private int id;
    
    public ChainDescription() {
        this.chainName = null;
        this.groupDescriptions = new ArrayList<>();
        this.groupConditions = new ArrayList<>();
        this.groupMeasures = new ArrayList<>();
        this.descriptionLookup = new HashMap<>();
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
    	    // TODO ?
//    		this.groupConditions.add((Condition) condition.clone());
    	}
    }
    
    public boolean hasName(String name) {
    	return this.chainName.equals(name);
    }
    
    public void addGroupDescription(GroupDescription groupDescription) {
        this.groupDescriptions.add(groupDescription);
        int id = this.id + groupDescriptions.size();
//        groupDescription.setID(id);
        this.descriptionLookup.put(id, groupDescription);
    }
    
    public void removeLastGroupDescription() {
    	GroupDescription g = this.groupDescriptions.remove(this.groupDescriptions.size() - 1);
    	List<Condition> toRemove = new ArrayList<>();
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
    
    public List<GroupDescription> getGroupDescriptions() {
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
    
    public GroupDescription first() {
        return this.groupDescriptions.get(0);
    }
    
    public GroupDescription last() {
        return this.groupDescriptions.get(this.size() - 1);
    }
    
    public ChainDescription getPathByGroupName(String groupName, String atomName) {
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
    
    public ChainDescription getPathByGroupLabel(String groupLabel, String atomName) {
        ChainDescription root = new ChainDescription(this.chainName);
        
        // TODO : what if multiple matches...
        for (GroupDescription groupDescription : this.groupDescriptions) {
            if (groupDescription.labelMatches(groupLabel)) {
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
            for (GroupDescription groupD : this) {
                Description d = groupD.getByID(id);
                if (d != null) {
                    return d;
                }
            }
        }
        return null;
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

    public void addCondition(Condition condition) {
        this.groupConditions.add(condition);
    }

    public List<Condition> getConditions() {
        return this.groupConditions;
    }

    public void addMeasure(Measure measure) {
        groupMeasures.add(measure);
    }

    public List<Measure> getMeasures() {
        return groupMeasures;
    }

    public void addSubDescription(Description subDescription) {
        if (subDescription instanceof GroupDescription) {
            this.addGroupDescription((GroupDescription) subDescription);
        } else {
            // TODO : type checking - throw error
        }
    }

    public List<GroupDescription> getSubDescriptions() {
        return this.groupDescriptions;
    }

    public Description getSubDescriptionAt(int i) {
        return groupDescriptions.get(i);
    }

    /**
     * @return the number of group descriptions in this chain
     */
    public int size() {
        return this.groupDescriptions.size();
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
