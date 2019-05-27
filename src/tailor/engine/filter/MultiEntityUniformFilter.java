package tailor.engine.filter;

import java.util.ArrayList;
import java.util.List;

import tailor.condition.alt.UniformCondition;
import tailor.structure.Group;

public class MultiEntityUniformFilter implements Filter<List<Group>, Group> {
    
    private final UniformCondition<Group> condition;
    
    public MultiEntityUniformFilter(UniformCondition<Group> condition) {
        this.condition = condition;
    }
    

    @Override
    public List<List<Group>> filter(Iterable<Group> iterable) {
        List<List<Group>> filtered = new ArrayList<>();
        int arity = condition.arity();
        List<Group> groups = new ArrayList<>();
        for (Group group : iterable) {
            addGroupToEnd(groups, group, arity);
            if (groups.size() == arity &&
                    condition.allows(groups.toArray(new Group[arity]))) {
                filtered.add(groups);
            }
        }
        return filtered;
    }
    

    private void addGroupToEnd(List<Group> groups, Group group, int arity) {
        if (groups.size() == arity) { // TODO - use a ring buffer data structure
            groups.remove(0);
        }
        groups.add(group);
    }

}
