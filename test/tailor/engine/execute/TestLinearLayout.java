package tailor.engine.execute;

import static tailor.engine.execute.StructureBuilder.makeStructure;

import java.util.List;

import org.junit.Test;

import tailor.structure.Group;

public class TestLinearLayout {
    
    @Test
    public void test() {
        List<Group> structure = makeStructure()
                                .group("ALA").atoms("A", "B", "C")
                                .group("GLY").atoms("D", "E", "F")
                                .get();
        LinearLayout.layout(structure);
        System.out.println(structure);
    }
}
