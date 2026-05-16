package tailor.engine.execute;

import java.util.List;

import javax.vecmath.Point3d;

import tailor.structure.Atom;
import tailor.structure.Group;

public class GridLayout {
    
    private static final Point3d ORIGIN = new Point3d(0, 0, 0);
    
    public enum Direction {
        XP( 1,  0,  0),
        YP( 0,  1,  0),
        ZP( 0,  0,  1),
        XM(-1,  0,  0),
        YM( 0, -1,  0),
        ZM( 0,  0, -1);
        
        public final Point3d vector;
        
        Direction(int x, int y, int z) {
            this.vector = new Point3d(x, y, z);
        }
    }
    
    public static void layout(List<Group> groups, Direction... directions) {
        assert numberOfAtoms(groups) - 1 == directions.length;
        Point3d currentPoint = new Point3d(ORIGIN.x, ORIGIN.y, ORIGIN.z);
        int index = -1;
        for (Group group : groups) {
            for (Atom atom : group.getAtoms()) {
                index++;
                atom.setPosition(currentPoint);
                if (index < directions.length) {
                    Point3d dv = directions[index].vector;
                    Point3d next = new Point3d(currentPoint);
                    next.add(dv);
                    currentPoint = next;
                }
            }
        }
    }
    
    private static int numberOfAtoms(List<Group> groups) {
        int count = 0;
        for (Group group : groups) {
            count += group.getAtoms().size();
        }
        return count;
    }

}
