package tailor.datasource.aigen;

import tailor.structure.Structure;

public class VisitorPattern {
    public static boolean printAction(Visitor visitor, Structure structure) {
        System.out.println(structure);
        return true;
    }

    public static boolean captureAction(Visitor visitor, Structure structure) {
        visitor.store = structure;
        return true;
    }

    public static boolean levelEqual(Visitor visitor, Structure structure) {
        return visitor.getLevel() == structure.getLevel();
    }
}
