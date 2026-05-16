package tailor.datasource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;

import tailor.structure.Chain;
import tailor.structure.ChainType;
import tailor.structure.Group;
import tailor.structure.Protein;
import tops.translation.model.Atom;

public class PDBReader {
	
	  public static Protein read(File path) throws IOException {
	        String pdbID = path.getName().substring(0, 4);	// this is a hack...
	        String line;

	        BufferedReader bufferer = null;
	        List<String> atomRecords = new ArrayList<>();
	        List<String> hetatmRecords = new ArrayList<>();
	        
	        try {
    	        bufferer = new BufferedReader(new FileReader(path));
    	        
    	        while ((line = bufferer.readLine()) != null) {
    	            if (line.length() > 4) {
    	                String token = line.substring(0, 4);
    	                if (token.equals("ATOM")) {
    	                    atomRecords.add(line);
    	                } else if (token.equals("HEAD")) {
                            pdbID = line.substring(62, 66);
    	                } else if (token.equals("HETA")) { // XXX
    	                    hetatmRecords.add(line);
    	                }
    	            }
    	        }
	        } finally {
	            if (bufferer!= null) bufferer.close();
	        }
	        System.out.println(atomRecords.size() + " atoms and " + hetatmRecords.size() + " hetatoms");

	        Protein protein = new Protein(pdbID);

	        Chain currentChain = null;

	        for (String atomRecord : atomRecords) {	        	
	            Chain newChain = PDBReader.parseRecord(atomRecord, currentChain, ChainType.PEPTIDE);
	            if (currentChain == null || !newChain.getName().equals(currentChain.getName())) {
	                protein.addChain(newChain);
	                currentChain = newChain;
	            } 
	        }
	        
	        currentChain = null;
	        
	        for (String hetatmRecord : hetatmRecords) {
	            Chain newChain = PDBReader.parseRecord(hetatmRecord, currentChain, ChainType.WATER);
                if (currentChain == null || !newChain.getName().equals(currentChain.getName())) {
                    protein.addChain(newChain);
                    currentChain = newChain;
                } 
	        }

	        return protein;
	    }

	    public static Chain parseRecord(String atomRecord, Chain chain, ChainType chainType) {
	        //String atomNumber = atomRecord.substring(4, 11).trim();
	        String atomName = atomRecord.substring(11, 16).trim();
	        String residueName = atomRecord.substring(17, 20);
	        String chainLabel = atomRecord.substring(20, 22).trim();
	        int residueNumber = Integer.valueOf(atomRecord.substring(22, 26).trim());
	        String coordinates = atomRecord.substring(27, 54).trim();

	        if (chainLabel.equals("")) {
	        	chainLabel = "A";
	        }
	        
            Group residue = null;
            if (chain == null || (!chainLabel.equals(chain.getName()))) {
	            chain = new Chain(chainLabel, chainType);
	        } else {
                residue = chain.getGroups().get(chain.getGroups().size() - 1);
            }

            if (residue == null || residue.getNumber() != residueNumber) {
                residue = PDBReader.createGroup(residueNumber, residueName);
                chain.addGroup(residue);
            }

            residue.addAtom(new Atom(atomName, fromCoords(coordinates)));
	        
	        return chain;
	    }
	    
	    private static Point3d fromCoords(String coords) {
			String[] bits = coords.split("\\s+");
			double x = Double.parseDouble(bits[0]);
			double y = Double.parseDouble(bits[1]);
			double z = Double.parseDouble(bits[2]);
			return new Point3d(x, y, z);
		}
	    
	    public static Group createGroup(int residueNumber, String residueName) {
	    	Group residue = new Group(residueNumber, residueName);
	    	return residue;
	    }
}
