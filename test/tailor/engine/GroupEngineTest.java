package tailor.engine;

import org.junit.Test;

import tailor.Level;
import tailor.condition.PropertyCondition;
import tailor.datasource.Structure;
import tailor.description.GroupDescription;

public class GroupEngineTest {
    
    @Test
    public void positiveMatch() {
        Structure aminoAcid = new Structure(Level.RESIDUE);
        aminoAcid.setProperty("Name", "AA");
        aminoAcid.setProperty("Number", "0");
        
        Structure atomN = new Structure(Level.ATOM);
        atomN.setProperty("Name", "N");
        aminoAcid.addSubStructure(atomN);
        
        Structure atomC = new Structure(Level.ATOM);
        atomC.setProperty("Name", "C");
        aminoAcid.addSubStructure(atomC);
        
        Structure atomO = new Structure(Level.ATOM);
        atomO.setProperty("Name", "O");
        aminoAcid.addSubStructure(atomO);
        
        GroupDescription aminoAcidDescription = new GroupDescription();
        aminoAcidDescription.addAtomDescription("N");
        aminoAcidDescription.addAtomDescription("C");
        aminoAcidDescription.addAtomDescription("O");
        aminoAcidDescription.addCondition(new PropertyCondition("Name", "AA"));
        
        GroupEngine groupEngine = new GroupEngine();
        Match match = groupEngine.match(aminoAcidDescription, aminoAcid);
        boolean complete = groupEngine.fullMatch(aminoAcidDescription, match);
        System.out.println(match + " is complete? " + complete);
    }

}
