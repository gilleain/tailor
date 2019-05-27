package tailor.engine.execute;

import static org.junit.Assert.assertEquals;
import static tailor.engine.execute.StructureBuilder.makeStructure;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import tailor.condition.alt.DistanceFromPoint;
import tailor.condition.alt.UniformCondition;
import tailor.description.AtomDescription;
import tailor.description.GroupDescription;
import tailor.engine.filter.UniformFilter;
import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Group;

public class TestUniformFilter {
    
    @Test
    public void testFixedPoint() {
        Vector p = new Vector(2, 1, 0);
        Group fixedGroup = new Group();
        fixedGroup.addAtom(new Atom("C", p));
        
        List<Group> unfiltered = getStructure();
        LinearLayout.layout(unfiltered);
        System.out.println(unfiltered.stream().map(Object::toString).collect(Collectors.joining("\n")));
        UniformFilter filter = new UniformFilter(makeCondition(fixedGroup));
        
        List<Group> filtered = filter.filter(unfiltered);
        assertEquals(2, filtered.size());
    }
    
    private List<Group> getStructure() {
        return makeStructure()
                .group("ALA").atoms("N", "C", "O")
                .group("GLY").atoms("N", "C", "O")
                .group("ALA").atoms("N", "C", "O")
                .group("TYR").atoms("N", "C", "O")
                .get();
    }
    
    private UniformCondition<Group> makeCondition(Group fixedGroup) {
        AtomDescription fixedDescription = new AtomDescription("C");
        
        GroupDescription variableDescription = new GroupDescription("ALA");
        AtomDescription variableAtomDescription = new AtomDescription("N");
        variableDescription.addAtomDescription(variableAtomDescription);
        
        Selector<Vector, Void> fixedPoint = new FixedAtomCenterSelector(fixedDescription, fixedGroup);
        Selector<Vector, Group> variablePoint = new AtomCenterPointSelector(variableDescription);
        
        return new DistanceFromPoint(6, fixedPoint, variablePoint);
    }

}
