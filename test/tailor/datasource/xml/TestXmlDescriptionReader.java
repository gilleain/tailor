package tailor.datasource.xml;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import tailor.condition.Condition;
import tailor.condition.HBondCondition;
import tailor.datasource.xml.XmlDescriptionReader;
import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;

public class TestXmlDescriptionReader {
    
    @Test
    public void testProteinOnly() {
        String xml = "<ProteinDescription name=\"test\"></ProteinDescription>";
        Description description = read(xml);
        assertNotNull(description);
        assertThat(description, instanceOf(ProteinDescription.class));
        assertTrue(description.getSubDescriptions().isEmpty());
    }
    
    @Test
    public void testProteinPlusChain() {
        String xml = "<ProteinDescription name=\"test\">"
                + "<ChainDescription name=\"A\"></ChainDescription>"
                + "</ProteinDescription>";
        Description description = read(xml);
        ChainDescription chainDescription = (ChainDescription) description.getSubDescriptionAt(0);
        assertEquals("A", chainDescription.getName());
    }
    
    @Test
    public void testChainPlusGroup() {
        String xml = "<ProteinDescription name=\"test\">"
                + "<ChainDescription name=\"A\">"
                + "<GroupDescription name=\"GLY\"></GroupDescription>\""
                + "<GroupDescription label=\"X\"></GroupDescription>\""
                + "</ChainDescription>"
                + "</ProteinDescription>";
        Description description = read(xml);
        ChainDescription chainDescription = (ChainDescription) description.getSubDescriptionAt(0);
        
        GroupDescription groupDescription1 = (GroupDescription) chainDescription.getSubDescriptionAt(0);
        assertEquals("GLY", groupDescription1.getName());
        
        GroupDescription groupDescription2 = (GroupDescription) chainDescription.getSubDescriptionAt(1);
        assertEquals("X", groupDescription2.getLabel());
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
        Description description = read(xml);
        ChainDescription chainDescription = (ChainDescription) description.getSubDescriptionAt(0);
        GroupDescription groupDescription = (GroupDescription) chainDescription.getSubDescriptionAt(0);
        AtomDescription atomDescription = (AtomDescription) groupDescription.getSubDescriptionAt(0);
        assertEquals("N", atomDescription.getName());
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
                + "<Path label=\"i + 1\" atom=\"N\"/>"
                + "<Path label=\"i + 1\" atom=\"H\"/>"
                + "<Path label=\"i\" atom=\"O\"/>"
                + "<Path label=\"i\" atom=\"C\"/>"
                + "</HBondCondition>"
                + "</ChainDescription>"
                + "</ProteinDescription>";
        
        Description description = read(xml);
        ChainDescription chainDescription = (ChainDescription) description.getSubDescriptionAt(0);
        Condition condition = chainDescription.getConditions().get(0);
        assertThat(condition, instanceOf(HBondCondition.class));
        
        HBondCondition hBondCondition = (HBondCondition) condition;
        assertEquals("i + 1", getGroupLabel(hBondCondition.getDonorAtomDescription()));
        assertEquals("i + 1", getGroupLabel(hBondCondition.getHydrogenAtomDescription()));
        assertEquals("i", getGroupLabel(hBondCondition.getAcceptorAtomDescription()));
        assertEquals("i", getGroupLabel(hBondCondition.getAttachedAtomDescription()));
    }
    
    private String getGroupLabel(Description chainDescription) {
        return ((GroupDescription)chainDescription.getSubDescriptionAt(0)).getLabel();
    }
    
    private Description read(String xml) {
        XmlDescriptionReader reader = new XmlDescriptionReader();
        return reader.readDescription(
                new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

}
