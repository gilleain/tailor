package tailor.engine.execute;

import java.util.ArrayList;
import java.util.List;

import tailor.condition.alt.Condition;
import tailor.structure.Group;

public class SingletonFilter implements Filter<Group, Group> {
    
    private Condition<Group> groupCondition;
    
    public SingletonFilter(Condition<Group> geom) {
        this.groupCondition = geom;
    }

    @Override
    public List<Group> filter(Iterable<Group> iterable) {
        List<Group> results = new ArrayList<>();
        for (Group group : iterable) {
            if (groupCondition.allows(group)) {
                results.add(group);
            }
        }
        return results;
    }

}
