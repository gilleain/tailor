package tailor.engine.execute;

import java.util.List;

import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Group;

public class LinearLayout {
    
    private static final Vector ORIGIN = new Vector(0, 0, 0);
    
    public static void layout(List<Group> groups) {
        layoutFrom(ORIGIN, groups);
    }

    public static void layoutFrom(Vector start, List<Group> groups) {
        Vector currentPoint = new Vector(start);
        Vector diff = new Vector(1, 0, 0);
        for (Group group : groups) {
            for (Atom atom : group.getAtoms()) {
                atom.setPosition(currentPoint);
                currentPoint = currentPoint.plus(diff);
            }
        }
    }

}
