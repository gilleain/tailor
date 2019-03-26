package tailor.engine.execute;

import java.util.List;

import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Group;

public class GridLayout {
    
    private static final Vector ORIGIN = new Vector(0, 0, 0);
    
    public enum Direction {
        XP( 1,  0,  0),
        YP( 0,  1,  0),
        ZP( 0,  0,  1),
        XM(-1,  0,  0),
        YM( 0, -1,  0),
        ZM( 0,  0, -1);
        
        public final Vector vector;
        
        Direction(int x, int y, int z) {
            this.vector = new Vector(x, y, z);
        }
    }
    
    public static void layout(List<Group> groups, Direction... directions) {
        assert numberOfAtoms(groups) - 1 == directions.length;
        Vector currentPoint = new Vector(ORIGIN);
        int index = -1;
        for (Group group : groups) {
            for (Atom atom : group.getAtoms()) {
                index++;
                atom.setPosition(currentPoint);
                if (index < directions.length) {
                    Vector dv = directions[index].vector;
                    currentPoint = currentPoint.plus(dv);
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
