package tailor.engine.execute;

import java.util.ArrayList;
import java.util.List;

import tailor.condition.alt.UniformCondition;
import tailor.structure.Group;

/**
 * Scan a chain according to some geometric condition.
 * 
 * @author gilleain
 *
 */
public class UniformFilter implements Filter<Group, Group> {
    
    private final UniformCondition<Group> condition;
    
    public UniformFilter(UniformCondition<Group> condition) {
        this.condition = condition;
    }
    

    @Override
    public List<Group> filter(Iterable<Group> iterable) {
        List<Group> filtered = new ArrayList<>();
        for (Group group : iterable) {
            if (condition.allows(group)) {
                filtered.add(group);
            }
        }
        return filtered;
    }

}
