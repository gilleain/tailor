package tailor.engine.execute;

import java.util.ArrayList;
import java.util.List;

import tailor.condition.alt.GeometricCondition;
import tailor.structure.Chain;
import tailor.structure.Group;

/**
 * Scan a chain according to some geometric condition.
 * 
 * @author gilleain
 *
 */
public class GeometricScanner {
    
    private final GeometricCondition<Group> condition;
    
    public GeometricScanner(GeometricCondition<Group> condition) {
        this.condition = condition;
    }
    
    public List<Group> scan(Chain chain) {
        List<Group> filtered = new ArrayList<>();
        for (Group group : chain.getGroups()) {
            if (condition.allows(group)) {
                filtered.add(group);
            }
        }
        return filtered;
    }

}
