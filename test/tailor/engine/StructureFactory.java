package tailor.engine;

import tailor.structure.Chain;
import tailor.structure.Group;
import tailor.structure.Structure;

public class StructureFactory {
    
    public static String oneToThree(char one) {
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
    
    public static Structure makeChain(String seq) {
        Chain chain = new Chain("A");
        for (int i = 0; i < seq.length(); i++) {
            String name = oneToThree(seq.charAt(i));
            Group residue = new Group();
            residue.setId(name);
            residue.setNumber(i);
            chain.addGroup(residue);
        }
        return chain;
    }
}
