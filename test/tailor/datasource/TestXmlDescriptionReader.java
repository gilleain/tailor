package tailor.datasource;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

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
                + "<GroupDescription name=\"GLY\" position=\"0\">"
                + "</GroupDescription>\""
                + "</ChainDescription>"
                + "</ProteinDescription>";
        Description description = read(xml);
        ChainDescription chainDescription = (ChainDescription) description.getSubDescriptionAt(0);
        GroupDescription groupDescription = (GroupDescription) chainDescription.getSubDescriptionAt(0);
        assertEquals("GLY", groupDescription.getName());
    }
    
    @Test
    public void testGroupPlusAtom() {
        String xml = "<ProteinDescription name=\"test\">"
                + "<ChainDescription name=\"A\">"
                + "<GroupDescription name=\"GLY\" position=\"0\">"
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
                + "<GroupDescription name=\"GLY\" position=\"0\">"
                + "<AtomDescription name=\"O\"/>"
                + "<AtomDescription name=\"C\"/>"
                + "</GroupDescription>\""
                + "<GroupDescription name=\"ALA\" position=\"1\">"
                + "<AtomDescription name=\"N\"/>"
                + "<AtomDescription name=\"H\"/>"
                + "</GroupDescription>\""
                + "<HBondCondition haMax=\"2.5\" dhaMin=\"120.0\" haaMin=\"90.0\">"
                + "<Path position=\"1\" atom=\"N\"/>"
                + "<Path position=\"1\" atom=\"H\"/>"
                + "<Path position=\"0\" atom=\"O\"/>"
                + "<Path position=\"0\" atom=\"C\"/>"
                + "</HBondCondition>"
                + "</ChainDescription>"
                + "</ProteinDescription>";
        
        Description description = read(xml);
        ChainDescription chainDescription = (ChainDescription) description.getSubDescriptionAt(0);
        GroupDescription groupDescription = (GroupDescription) chainDescription.getSubDescriptionAt(0);
        AtomDescription atomDescription = (AtomDescription) groupDescription.getSubDescriptionAt(0);
        assertEquals("N", atomDescription.getName());
    }
    
    private Description read(String xml) {
        XmlDescriptionReader reader = new XmlDescriptionReader();
        return reader.readDescription(
                new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

}
