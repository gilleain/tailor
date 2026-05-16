package tailor.structure;

import java.util.ArrayList;
import java.util.List;

import tops.translation.model.Group;
import tops.translation.model.PolymerType;
import tops.translation.model.Segment;

public class Chain  {
    
    private final List<Segment> sses;
    
    private final List<Group> groups;
    
    private String name;
    
    public void setName(String name) {
        this.name = name;
    }

    private PolymerType chainType;
    
    public Chain() {
        this("A");  // XXX is this a sensible default?
    }
    
    public Chain(String name) {	// TODO - type is null here 
        this.name = name;
        this.sses = new ArrayList<>();
        this.groups = new ArrayList<>();
    }
    
    public Chain(String name, PolymerType chainType) {
        this.name = name;
        this.sses = new ArrayList<>();
        this.chainType = chainType;
        this.groups = new ArrayList<>();
    }
    
    public PolymerType getChainType() {
        return this.chainType;
    }
    
    public String getResidueRange() {
        if (groups.size() > 1) {
            return String.format("%s-%s", getFirst().getNumber(), getLast().getNumber());
        } else {
            return String.valueOf(getFirst().getNumber());
        }
    }
    
    public PolymerType getType() {
    	return this.chainType;
    }
    
    public List<Group> getGroups() {
    	return this.groups;
    }
    
    public Group getFirst() {
        return groups.get(0);
    }
    
    public Group getLast() {
        return groups.get(groups.size() - 1);
    }
    
    public Group getGroupAt(int index) {
    	return this.groups.get(index);	// TODO - is this the best way to do this?
    }
    
    public void addSSE(Segment sse) {
        this.sses.add(sse);
    }
    
    public List<Segment> getSSEs() {
        return this.sses;
    }
    
    public void addGroup(Group group) {
        groups.add(group);
    }

    public String getName() {
        return name;
    }

}
