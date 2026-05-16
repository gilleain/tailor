package translation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;

import tops.translation.model.Atom;
import tops.translation.model.Chain;
import tops.translation.model.Group;
import tops.translation.model.Protein;

public class PDBReader {

    public static Protein read(String filename) throws IOException {
    	return PDBReader.read(new File(filename));
    }
    
    public static Protein read(File file) throws IOException {	
        List<String> atomRecords;
        String pdbID = "Unknown";

        BufferedReader bufferer = null;
        try {
            bufferer = new BufferedReader(new FileReader(file));
            String line;
            atomRecords = new ArrayList<>();
            while ((line = bufferer.readLine()) != null) {
                if (line.length() > 4) {
                    String token = line.substring(0, 4);
                    if (token.equals("ATOM")) {
                        atomRecords.add(line);
                    } else if (token.equals("HEAD")) {
                        pdbID = line.substring(line.length() - 4, line.length());
                    }
                }
            }
        } finally {
            if (bufferer != null) bufferer.close();
        }

        Protein protein = new Protein(pdbID);

        Chain currentChain = null;

        for (String atomRecord : atomRecords) {
            Chain newChain = PDBReader.parseRecord(atomRecord, currentChain);
            if (!newChain.equals(currentChain)) {
                protein.addChain(newChain);
                currentChain = newChain;
            }
        }

        return protein;
    }

    public static Chain parseRecord(String atomRecord, Chain chain) {
//        String atomNumber = atomRecord.substring(4, 11).trim();
        String atomType = atomRecord.substring(11, 16).trim();
        String residueType = atomRecord.substring(17, 20);
        String chainLabel = atomRecord.substring(20, 22);
        String residueNumber = atomRecord.substring(22, 26).trim();
        String coordinates = atomRecord.substring(27, 54).trim();

        if (chain == null || (!chainLabel.equals(chain.getName()))) {
            chain = new Chain(chainLabel);
        }

        Group r;
        int pdbNumber = Integer.parseInt(residueNumber);
        if (chain.hasResidueByPDBNumbering(pdbNumber)) {
            r = chain.getResidueByPDBNumbering(pdbNumber);
        } else {
            r = chain.createResidue(pdbNumber, residueType);
        }

        r.addAtom(new Atom(atomType, fromCoords(coordinates)));

        return chain;
    }
    
    private static Point3d fromCoords(String coords) {
		String[] bits = coords.split("\\s+");
		double x = Double.parseDouble(bits[0]);
		double y = Double.parseDouble(bits[1]);
		double z = Double.parseDouble(bits[2]);
		return new Point3d(x, y, z);
	}

    public static void main(String[] args) {
        try {
            Protein protein = PDBReader.read(args[0]);
            System.out.println(protein.toString());
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
}
