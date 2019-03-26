package tailor.engine.execute;

import static tailor.engine.execute.GridLayout.Direction.XP;
import static tailor.engine.execute.GridLayout.Direction.YP;
import static tailor.engine.execute.GridLayout.Direction.ZP;
import static tailor.engine.execute.GridLayout.Direction.XM;
import static tailor.engine.execute.GridLayout.Direction.YM;
import static tailor.engine.execute.GridLayout.Direction.ZM;
import static tailor.engine.execute.StructureBuilder.makeStructure;

import java.util.List;

import org.junit.Test;

import tailor.structure.Group;

public class TestGridLayout {
    
    @Test
    public void test() {
        List<Group> structure = makeStructure()
                .group("ALA").atoms("A", "B", "C")
                .group("GLY").atoms("D", "E", "F", "G")
                .get();
        GridLayout.layout(structure, XP, YP, ZP, YM, XM, ZM);
        for (Group g : structure) {
            System.out.println(g);
        }
    }

}
