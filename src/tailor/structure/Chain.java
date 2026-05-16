package tailor.structure;

import java.util.ArrayList;
import java.util.List;

import tops.translation.model.Group;

public class Chain extends Segment {
    
    private final List<Segment> sses;
    
    private String name;
    
    public void setName(String name) {
        this.name = name;
    }

    private ChainType chainType;
    
    public Chain() {
        this("A");  // XXX is this a sensible default?
    }
    
    public Chain(String name) {	// TODO - type is null here 
        super(Type.CHAIN, new ArrayList<>());
        this.name = name;
        this.sses = new ArrayList<>();
    }
    
    public Chain(String name, ChainType chainType) {
        super(Type.CHAIN, new ArrayList<>());
        this.name = name;
        this.sses = new ArrayList<>();
        this.chainType = chainType;
    }
    
    public ChainType getChainType() {
        return this.chainType;
    }
    
    public String getResidueRange() {
        if (size() > 1) {
            return String.format("%s-%s", getFirst().getNumber(), getLast().getNumber());
        } else {
            return String.valueOf(getFirst().getNumber());
        }
    }
    
    public Group getGroupAt(int index) {
    	return this.getGroups().get(index);	// TODO - is this the best way to do this?
    }
    
    public void addSSE(Segment sse) {
        this.sses.add(sse);
    }
    
    public List<Segment> getSSEs() {
        return this.sses;
    }
    
    public void addGroup(Group group) {
        super.addGroup(group);
    }

    public String getName() {
        return name;
    }

}
