package tailor.condition.alt;

import tailor.engine.execute.Selector;
import tailor.geometry.Geometry;
import tailor.geometry.Vector;
import tailor.structure.Group;

public class DistanceBetween implements UniformCondition<Group> {
    
    private final double minDistance;
    
    private final Selector<Vector, Group> pointA;
    
    private final Selector<Vector, Group> pointB;
    
    public DistanceBetween(double minDistance, Selector<Vector, Group> pointA, Selector<Vector, Group> pointB) {
        this.minDistance = minDistance;
        this.pointA = pointA;
        this.pointB = pointB;
    }

    @Override
    public boolean allows(Group... groups) {
        Group groupA = groups[0];  // TODO - check entities length?
        Group groupB = groups[1];
        
        Vector p1 = pointA.get(groupA);
        Vector p2 = pointB.get(groupB);
        
        if (p1 == null || p2 == null) {
            return false;
        } else {
            return Geometry.distance(p1, p2) < minDistance;
        }
    }

    @Override
    public int arity() {
        return 2;
    }

}
