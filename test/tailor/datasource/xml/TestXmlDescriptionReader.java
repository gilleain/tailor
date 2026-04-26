package tailor.datasource.xml;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import tailor.api.AtomListDescription;
import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;
import tailor.description.atom.AtomDistanceRangeDescription;
import tailor.description.atom.AtomTorsionRangeDescription;
import tailor.description.atom.HBondDescription;

public class TestXmlDescriptionReader {
    
    @Test
    public void testProteinOnly() {
        String xml = "<ProteinDescription name=\"test\"></ProteinDescription>";
        ChainDescription description = read(xml);
        assertNotNull(description);
        assertThat(description, instanceOf(ProteinDescription.class));
        assertTrue(description.getGroupDescriptions().isEmpty());
    }
    
    @Test
    public void testProteinPlusChain() {
        String xml = "<ProteinDescription name=\"test\">"
                + "<ChainDescription name=\"A\"></ChainDescription>"
                + "</ProteinDescription>";
        ChainDescription chainDescription = read(xml);
        assertEquals("A", chainDescription.getLabel().get());
    }
    
    @Test
    public void testChainPlusGroup() {
        String xml = "<ProteinDescription name=\"test\">"
                + "<ChainDescription name=\"A\">"
                + "<GroupDescription name=\"GLY\"></GroupDescription>\""
                + "<GroupDescription label=\"X\"></GroupDescription>\""
                + "</ChainDescription>"
                + "</ProteinDescription>";
        ChainDescription chainDescription = read(xml);
        
        GroupDescription groupDescription1 = chainDescription.getGroupDescriptions().get(0);
        assertEquals("GLY", groupDescription1.getName().get());
        
        GroupDescription groupDescription2 = chainDescription.getGroupDescriptions().get(1);
        assertEquals("X", groupDescription2.getLabel().get());
    }
    
    @Test
    public void testGroupPlusAtom() {
        String xml = "<ProteinDescription name=\"test\">"
                + "<ChainDescription name=\"A\">"
                + "<GroupDescription name=\"GLY\">"
                + "<AtomDescription name=\"N\"/>"
                + "</GroupDescription>\""
                + "</ChainDescription>"
                + "</ProteinDescription>";
        ChainDescription chainDescription = read(xml);
        GroupDescription groupDescription = chainDescription.getGroupDescriptions().get(0);
        AtomDescription atomDescription = groupDescription.getAtomDescription("N");
        assertEquals("N", atomDescription.getLabel());
    }
    
    @Test
    public void testHBondCondition() {
        String xml = "<ProteinDescription name=\"test\">"
                + "<ChainDescription name=\"A\">"
                + "<GroupDescription name=\"GLY\" label=\"i\">"
                + "<AtomDescription name=\"O\"/>"
                + "<AtomDescription name=\"C\"/>"
                + "</GroupDescription>\""
                + "<GroupDescription name=\"ALA\" label=\"i + 1\">"
                + "<AtomDescription name=\"N\"/>"
                + "<AtomDescription name=\"H\"/>"
                + "</GroupDescription>\""
                + "<HBondCondition haMax=\"2.5\" dhaMin=\"120.0\" haaMin=\"90.0\">"
                + "<Path name=\"donor\" label=\"i + 1\" atom=\"N\"/>"
                + "<Path name=\"hydrogen\" label=\"i + 1\" atom=\"H\"/>"
                + "<Path name=\"acceptor\" label=\"i\" atom=\"O\"/>"
                + "<Path name=\"attached\" label=\"i\" atom=\"C\"/>"
                + "</HBondCondition>"
                + "</ChainDescription>"
                + "</ProteinDescription>";
        
        ChainDescription chainDescription = read(xml);
        AtomListDescription description = chainDescription.getAtomListDescriptions().get(0);
        assertThat(description, instanceOf(HBondDescription.class));
        
        HBondDescription hBondDescription = (HBondDescription) description;
        
        // TODO
        assertEquals("i + 1", getGroupLabel(hBondDescription.getGroupDescriptions().get(0)));
        assertEquals("i + 1", getGroupLabel(hBondDescription.getGroupDescriptions().get(1)));
        assertEquals("i", getGroupLabel(hBondDescription.getGroupDescriptions().get(2)));
        assertEquals("i", getGroupLabel(hBondDescription.getGroupDescriptions().get(3)));
    }
    
    @Test
    public void testDistanceCondition() {
        String xml = "<ProteinDescription name=\"test\">"
                + "<ChainDescription name=\"A\">"
                + "<GroupDescription name=\"GLY\" label=\"i\">"
                + "<AtomDescription name=\"C\"/>"
                + "</GroupDescription>\""
                + "<GroupDescription name=\"GLY\" label=\"i + 1\">"
                + "<AtomDescription name=\"C\"/>"
                + "</GroupDescription>\""
                + "<DistanceCondition name=\"d\" center=\"3.5\" range=\"1.0\">"
                + "<Path name=\"a\" label=\"i\" atom=\"C\"/>"
                + "<Path name=\"b\" label=\"i + 1\" atom=\"C\"/>"
                + "</DistanceCondition>"
                + "</ChainDescription>"
                + "</ProteinDescription>";
        
        ChainDescription chainDescription = read(xml);
        AtomListDescription condition = chainDescription.getAtomListDescriptions().get(0);
        assertThat(condition, instanceOf(AtomDistanceRangeDescription.class));
        
        AtomDistanceRangeDescription distanceDescription = (AtomDistanceRangeDescription) condition;
        
        // TODO
        assertEquals("i", getGroupLabel(distanceDescription.getGroupDescriptions().get(0)));
        assertEquals("i + 1", getGroupLabel(distanceDescription.getGroupDescriptions().get(1)));
    }
    
    @Test
    public void testTorsionCondition() {
        String xml = "<ProteinDescription name=\"test\">"
                + "<ChainDescription name=\"A\">"
                + "<GroupDescription name=\"GLY\" label=\"i\">"
                + "<AtomDescription name=\"C\"/>"
                + "</GroupDescription>\""
                + "<GroupDescription name=\"GLY\" label=\"i + 1\">"
                + "<AtomDescription name=\"C\"/>"
                + "</GroupDescription>\""
                + "<GroupDescription name=\"GLY\" label=\"i + 2\">"
                + "<AtomDescription name=\"C\"/>"
                + "</GroupDescription>\""
                + "<GroupDescription name=\"GLY\" label=\"i + 3\">"
                + "<AtomDescription name=\"C\"/>"
                + "</GroupDescription>\""
                + "<TorsionCondition name=\"gamma\" midPoint=\"120.0\" range=\"10.0\">"
                + "<Path name=\"a\" label=\"i\" atom=\"C\"/>"
                + "<Path name=\"b\" label=\"i + 1\" atom=\"C\"/>"
                + "<Path name=\"c\" label=\"i + 2\" atom=\"C\"/>"
                + "<Path name=\"d\" label=\"i + 3\" atom=\"C\"/>"
                + "</TorsionCondition>"
                + "</ChainDescription>"
                + "</ProteinDescription>";
        
        ChainDescription chainDescription = read(xml);
        AtomListDescription atomListDescription = chainDescription.getAtomListDescriptions().get(0);
        assertThat(atomListDescription, instanceOf(AtomTorsionRangeDescription.class));
        
        AtomTorsionRangeDescription torsion = (AtomTorsionRangeDescription) atomListDescription;

        // TODO
        assertEquals("i", getGroupLabel(torsion.getGroupDescriptions().get(0)));
        assertEquals("i + 1", getGroupLabel(torsion.getGroupDescriptions().get(1)));
        assertEquals("i + 2", getGroupLabel(torsion.getGroupDescriptions().get(2)));
        assertEquals("i + 3", getGroupLabel(torsion.getGroupDescriptions().get(3)));
    }
    
    private String getGroupLabel(GroupDescription groupDescription) {
        return groupDescription.getLabel().get();
    }
    
    private ChainDescription read(String xml) {
        XmlDescriptionReader reader = new XmlDescriptionReader();
        return reader.readDescription(
                new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

}
