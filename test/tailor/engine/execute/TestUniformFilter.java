package tailor.engine.execute;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tailor.condition.alt.DistanceFromPoint;
import tailor.condition.alt.UniformCondition;
import tailor.description.AtomDescription;
import tailor.description.GroupDescription;
import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Group;

public class TestUniformFilter {
    
    @Test
    public void testFixedPoint() {
        UniformFilter filter = new UniformFilter(makeCondition());
        
        List<Group> unfiltered = new ArrayList<>();
        
        List<Group> filtered = filter.filter(unfiltered);
    }
    
    private UniformCondition<Group> makeCondition() {
        Group fixedGroup = new Group();
        fixedGroup.addAtom(new Atom("C"));
        
        AtomDescription fixedDescription = new AtomDescription("C");
        GroupDescription variableDescription = new GroupDescription("ALA");
        AtomDescription variableAtomDescription = new AtomDescription("N");
        variableDescription.addAtomDescription(variableAtomDescription);
        
        Selector<Vector, Void> fixedPoint = new FixedAtomCenterSelector(fixedDescription, fixedGroup);
        Selector<Vector, Group> variablePoint = new AtomCenterPointSelector(variableDescription);
        
        return new DistanceFromPoint(3, fixedPoint, variablePoint);
    }

}