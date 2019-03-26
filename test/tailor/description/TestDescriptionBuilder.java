package tailor.description;

import static org.junit.Assert.assertEquals;
import static tailor.description.DescriptionBuilder.makeDescription;

import org.junit.Test;

public class TestDescriptionBuilder {
    
    @Test
    public void testFullToAtoms() {
        Description root = makeDescription().protein().chain("A").group("GLY").atoms("N", "CA", "C", "O").get();
        assertEquals(ProteinDescription.class, root.getClass());
        Description subDescription1 = root.getSubDescriptionAt(0);
        assertEquals(ChainDescription.class, subDescription1.getClass());
        Description subDescription2 = subDescription1.getSubDescriptionAt(0);
        assertEquals(GroupDescription.class, subDescription2.getClass());
        for (Description subDescription : subDescription2.getSubDescriptions()) {
            assertEquals(AtomDescription.class, subDescription.getClass());
        }
    }

}
