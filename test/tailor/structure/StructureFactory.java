package tailor.structure;

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
    
    public static String[] atoms(char one) {
        switch (one) {
            case 'A': return new String[] {"N", "CA", "C", "O", "H", "HA", "CB", "1HB", "2HB", "3HB"};
            case 'C': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'D': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'E': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'F': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'G': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'H': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'I': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'K': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'L': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'M': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'N': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'P': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'Q': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'R': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'S': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'T': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'V': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'W': return new String[] {"N", "CA", "C", "O", "HA"};
            case 'Y': return new String[] {"N", "CA", "C", "O", "HA"};
            
            default: return new String[] {"N", "CA", "C", "O", "HA"};   // backbone
        }
    }
    
    public static Chain makeChain(String seq) {
        Chain chain = new Chain();
        for (int i = 0; i < seq.length(); i++) {
            String name = oneToThree(seq.charAt(i));
            Group residue = new Group();
            residue.setId(name);
            chain.addGroup(residue);
        }
        return chain;
    }
}
