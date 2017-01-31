package tailor.source;

import java.io.BufferedWriter;
import java.io.IOException;

import tailor.structure.Atom;
import tailor.structure.Chain;
import tailor.structure.Group;
import tailor.structure.Protein;

public class PDBWriter {
    
    private static final String FORMAT = "ATOM %6d %s %s %5s     % 7.3f % 7.3f % 7.3f";
    
    public static void write(Protein protein, BufferedWriter out) throws IOException {
        int index = 1;
        for (Chain chain : protein.getChains()) {
            index = PDBWriter.write(chain, index, out);
        }
    }

    public static int write(Chain chain, int index, BufferedWriter out) throws IOException {
        int chainIndex = index;
        for (Group group : chain.getGroups()) {
            String residueName = group.getName();
            int residueNumber = group.getNumber();
            for (Atom atom : group.getAtoms()) {
                PDBWriter.write(atom, chainIndex, residueName, residueNumber, out);
                out.newLine();
                chainIndex++;
            }
        }
        return chainIndex;
    }
    
    public static void write(Atom atom, int index, 
            String residueName, int residueNumber, BufferedWriter out) throws IOException {
        out.write(String.format(
                FORMAT, index, centerPad(atom.getName()), residueName, residueNumber, 
                atom.getCenter().x(), atom.getCenter().y(), atom.getCenter().z()));
        
    }
    
    /**
     * Pad specifically for PDB files...
     * 
     * @param name
     * @return
     */
    private static String centerPad(String name) {
        if (name.length() == 1) {
            return " " + name + "  ";
        } else if (name.length() == 2) {
            return " " + name + " ";
        } else if (name.length() == 3) {
            return name + " ";
        } else {
            return name;
        }
    }

}
