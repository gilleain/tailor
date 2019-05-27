package tailor.engine.execute;

import static org.junit.Assert.assertEquals;
import static tailor.description.DescriptionBuilder.makeDescription;
import static tailor.engine.execute.StructureBuilder.makeStructure;

import java.util.List;

import org.junit.Test;

import tailor.condition.alt.DistanceBetween;
import tailor.condition.alt.UniformCondition;
import tailor.engine.filter.MultiEntityUniformFilter;
import tailor.geometry.Vector;
import tailor.structure.Group;

public class TestMultiEntityUniformFilter {
    
    @Test
    public void testVariablePoints() {
        List<Group> unfiltered = getStructure();
        LinearLayout.layout(unfiltered);
        unfiltered.stream().forEach(System.out::println);
        MultiEntityUniformFilter filter = 
                new MultiEntityUniformFilter(makeVariableCondition());
        
        List<List<Group>> filtered = filter.filter(unfiltered);
        assertEquals("One match", 1, filtered.size());
        
        List<Group> match = filtered.get(0);
        assertEquals("Match size", 2, match.size());
    }
    
    private UniformCondition<Group> makeVariableCondition() {
        double minDistance = 6;
        
        Selector<Vector, Group> varPointA = new AtomCenterPointSelector(makeDescription().group("ALA").atoms("N").getGroup());
        Selector<Vector, Group> varPointB = new AtomCenterPointSelector(makeDescription().group("TYR").atoms("C").getGroup());
       
        return new DistanceBetween(minDistance, varPointA, varPointB);
    }
    
    private List<Group> getStructure() {
        return makeStructure()
                .group("ALA").atoms("N", "C", "O")
                .group("GLY").atoms("N", "C", "O")
                .group("ALA").atoms("N", "C", "O")
                .group("TYR").atoms("N", "C", "O")
                .get();
    }

}
