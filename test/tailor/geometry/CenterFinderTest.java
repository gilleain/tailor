package tailor.geometry;

import org.junit.Test;

import tailor.condition.PropertyCondition;
import tailor.datasource.Structure;
import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.DescriptionException;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;
import tailor.engine.Match;
import tailor.measure.DistanceMeasure;
import tailor.structure.Level;

public class CenterFinderTest {
    
    private void connectHierarchy(Structure... structures) {
        for (int i = 0; i < structures.length; i++) {
            if (i + 1 < structures.length) {
                structures[i].addSubStructure(structures[i+1]);
            }
        }
    }
    
    private void connectHierarchy(Description... descriptions) {
        for (int i = 0; i < descriptions.length; i++) {
            if (i + 1 < descriptions.length) {
                descriptions[i].addSubDescription(descriptions[i+1]);
            }
        }
    }
    
    private void printHierarchy(Structure structure) {
        System.out.println(structure.getLevel());
        for (Structure subStructure : structure) {
            printHierarchy(subStructure);
        }
    }
    
    private void printHierarchy(Description description) {
        System.out.println(description.getLevel() + " " + description.getID());
        for (Description subDescription : description.getSubDescriptions()) {
            printHierarchy(subDescription);
        }
    }

    
    private void zip(Match match, Description description, Structure structure) {
        Match subMatch = match.associate(description, structure);
        if (description.getSubDescriptions().size() > 0) {
            zip(subMatch, description.getSubDescriptionAt(0), 
                          structure.getSubStructureAtIndex(0));
        }
    }
    
    @Test
    public void findSingleCenterTest() {
        Description protD = new ProteinDescription();
        Description chainD = new ChainDescription();
        Description groupD = new GroupDescription();
        Description atomD = new AtomDescription();
        atomD.addCondition(new PropertyCondition("Name", "N"));
        connectHierarchy(protD, chainD, groupD, atomD);
        
        Structure prot = new Structure(Level.PROTEIN);
        Structure chain = new Structure(Level.CHAIN);
        Structure group = new Structure(Level.RESIDUE);
        Structure atom = new Structure(Level.ATOM);
        atom.setProperty("Name", "N");
        atom.setProperty("Coords", "1 2 3");
        connectHierarchy(prot, chain, group, atom);
        
//        printHierarchy(prot);
        Match match = new Match(protD, prot);
        zip(match, chainD, chain);
        Vector center = CenterFinder.findCenter(protD, match);
        System.out.println(center);
    }
    
    @Test
    public void simpleMeasureTest() throws DescriptionException {
        Description protD = new ProteinDescription();
        Description chainD = new ChainDescription();
        Description groupD = new GroupDescription();
        
        Description atomND = new AtomDescription();
        atomND.addCondition(new PropertyCondition("Name", "N"));
        connectHierarchy(protD, chainD, groupD, atomND);
        
        Description atomOD = new AtomDescription();
        atomOD.addCondition(new PropertyCondition("Name", "O"));
        connectHierarchy(groupD, atomOD);
        
        Structure prot = new Structure(Level.PROTEIN);
        Structure chain = new Structure(Level.CHAIN);
        Structure group = new Structure(Level.RESIDUE);
        Structure atomA = new Structure(Level.ATOM);
        Structure atomB = new Structure(Level.ATOM);
        
        atomA.setProperty("Name", "N");
        atomA.setProperty("Coords", "0 0 0");
        atomB.setProperty("Name", "O");
        atomB.setProperty("Coords", "0 0 1");
        connectHierarchy(prot, chain, group, atomA);
        
        Match match = new Match(protD, prot);
        zip(match, chainD, chain);
        connectHierarchy(group, atomB);
        match.getSubMatch(0).getSubMatch(0).associate(atomOD, atomB);
        
        printHierarchy(protD);
        DistanceMeasure measure = 
            new DistanceMeasure(atomOD.getID(), atomND.getID(), protD);
        System.out.println(measure.measure(match));
    }

}
