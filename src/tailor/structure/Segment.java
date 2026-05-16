package tailor.structure;

import java.util.List;

import tops.translation.model.Group;

/**
 * A segment is a sequence of groups, for example a Chain or an SSE.
 * 
 * @author gilleain
 *
 */
public class Segment  {
    
    private final List<Group> groups;
    
    private tops.translation.model.Segment.Type type;
    
    public Segment(tops.translation.model.Segment.Type type) {
    	this(type, List.of());
    }
    
    public Segment(tops.translation.model.Segment.Type type, List<Group> groups) {
    	this.type = type;
        this.groups = groups;
    }
    
    public tops.translation.model.Segment.Type getType() {
    	return this.type;
    }
    
    public int size() {
    	return groups.size();
    }
    
    public void addGroup(Group group) {
        groups.add(group);
    }
    
    public Group getFirst() {
        return groups.get(0);
    }
    
    public Group getLast() {
        return groups.get(groups.size() - 1);
    }
    
    public List<Group> getGroups() {
        return this.groups;
    }

}
