package tailor.engine;

import java.util.List;

import org.junit.Test;

import tailor.Level;
import tailor.datasource.Structure;
import tailor.description.ChainDescription;
import tailor.description.GroupDescription;

public class RangedChainSingleEngineTest {
    
    public String oneToThree(char one) {
        switch (one) {
            case 'A': return "ALA";
            case 'C': return "CYS";
            case 'D': return "ASP";
            case 'E': return "GLU";
            case 'F': return "PHE";
            case 'G': return "GLY";
            case 'H': return "HIS";
            case 'I': return "ILE";
            case 'K': return "LYS";
            case 'L': return "LEU";
            case 'M': return "MET";
            case 'N': return "ASN";
            case 'P': return "PRO";
            case 'Q': return "GLN";
            case 'R': return "ARG";
            case 'S': return "SER";
            case 'T': return "THR";
            case 'V': return "VAL";
            case 'W': return "TRP";
            case 'Y': return "TYR";
            default: return "ALA";
        }
    }
    
    public Structure makeChain(String seq) {
        Structure chain = new Structure(Level.CHAIN);
        chain.setProperty("Name", "A");
        for (int i = 0; i < seq.length(); i++) {
            String name = oneToThree(seq.charAt(i));
            Structure residue = new Structure(Level.RESIDUE);
            residue.setProperty("Name", name);
            residue.setProperty("Number", String.valueOf(i));
            chain.addSubStructure(residue);
        }
        return chain;
    }
    
    @Test
    public void simpleTest() {
        // make the data
        Structure protein = makeChain("GTYPEDFASED");
        
        // make the pattern
        ChainDescription chainDescription = new ChainDescription();
        GroupDescription groupDescriptionA = new GroupDescription();
        GroupDescription groupDescriptionB = new GroupDescription();
        chainDescription.addGroupDescription(groupDescriptionA);
        chainDescription.addGroupDescription(groupDescriptionB);
        
        // match
        RangedSingleChainEngine engine = new RangedSingleChainEngine();
        List<Structure> matches = engine.scan(chainDescription, protein);
        for (Structure match : matches) {
            System.out.println(match);
        }
    }

}
