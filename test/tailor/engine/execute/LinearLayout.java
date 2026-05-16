package tailor.engine.execute;

import java.util.List;

import javax.vecmath.Point3d;

import tops.translation.model.Atom;
import tops.translation.model.Group;

public class LinearLayout {
    
    private static final Point3d ORIGIN = new Point3d(0, 0, 0);
    
    public static void layout(List<Group> groups) {
        layoutFrom(ORIGIN, groups);
    }

    public static void layoutFrom(Point3d start, List<Group> groups) {
        Point3d currentPoint = new Point3d(start);
        Point3d diff = new Point3d(1, 0, 0);
        for (Group group : groups) {
            for (Atom atom : group.getAtoms()) {
                atom.setPosition(currentPoint);
                currentPoint.add(diff);
            }
        }
    }

}
