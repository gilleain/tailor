package tailor.condition.alt;


import tailor.engine.execute.Selector;
import tailor.geometry.Geometry;
import tailor.geometry.Vector;
import tailor.structure.Group;

public class DistanceFromPoint implements UniformCondition<Group> {
    
    private final double minDistance;
    
    private final Selector<Vector, Group> variablePoint;
    
    private final Selector<Vector, Void> fixedPoint;
    
    public DistanceFromPoint(double minDistance, Selector<Vector, Void> fixedPoint, Selector<Vector, Group> variablePoint) {
        this.minDistance = minDistance;
        this.fixedPoint = fixedPoint;
        this.variablePoint = variablePoint;
    }

    @Override
    public boolean allows(Group... entities) {
        Group group = entities[0];  // TODO - check entities length?
        
        Vector p1 = fixedPoint.get();
        Vector p2 = variablePoint.get(group);
        if (p2 == null) {
            return false;
        } else {
            double d = Geometry.distance(p1, p2);
            System.err.println(group + " = " + d);
            return d < minDistance;
        }
    }

    @Override
    public int arity() {
        return 1;
    }
}
