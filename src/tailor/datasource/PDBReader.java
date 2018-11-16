package tailor.datasource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tailor.structure.Level;

public class PDBReader {
	
	  public static Structure read(File path) throws IOException {
	        String pdbID = path.getName().substring(0, 4);	// this is a hack...
	        String line;

	        BufferedReader bufferer = null;
	        List<String> atomRecords = new ArrayList<String>();
	        
	        try {
    	        bufferer = new BufferedReader(new FileReader(path));
    	        
    	        while ((line = bufferer.readLine()) != null) {
    	            if (line.length() > 4) {
    	                String token = line.substring(0, 4);
    	                if (token.equals("ATOM")) {
    	                    atomRecords.add(line);
    	                } else if (token.equals("HEAD")) {
                            pdbID = line.substring(62, 66);
    	                }
    	            }
    	        }
	        } finally {
	            if (bufferer!= null) bufferer.close();
	        }

	        Structure protein = new Structure(Level.PROTEIN);
	        protein.setProperty("Name", pdbID);

	        Structure currentChain = null;

	        for (String atomRecord : atomRecords) {	        	
	            Structure newChain = PDBReader.parseRecord(atomRecord, currentChain);
	            if (currentChain == null || !newChain.getProperty("Name").equals(currentChain.getProperty("Name"))) {
	                protein.addSubStructure(newChain);
	                currentChain = newChain;
	            } 
	        }

	        return protein;
	    }

	    public static Structure parseRecord(String atomRecord, Structure chain) {
	        //String atomNumber = atomRecord.substring(4, 11).trim();
	        String atomName = atomRecord.substring(11, 16).trim();
	        String residueName = atomRecord.substring(17, 20);
	        String chainLabel = atomRecord.substring(20, 22).trim();
	        String residueNumber = atomRecord.substring(22, 26).trim();
	        String coordinates = atomRecord.substring(27, 54).trim();

	        if (chainLabel.equals("")) {
	        	chainLabel = "A";
	        }
	        
            Structure residue = null;
            int chainIndex;
            if (chain == null || (!chainLabel.equals(chain.getProperty("Name")))) {
	            chain = new Structure(Level.CHAIN);
	            chain.setProperty("Name", chainLabel);
                chainIndex = 0; 
	        } else {
                residue = chain.getLastSubStructure(); 
                chainIndex = Integer.valueOf(residue.getProperty("Index"));
            }

            if (residue == null || !residue.hasPropertyEqualTo("Number", residueNumber)) {
                if (residue != null) {
                    chainIndex++;
                }
                residue = PDBReader.createResidue(residueNumber, residueName, chainIndex);
                chain.addSubStructure(residue);
            }

            Structure atom = new Structure(Level.ATOM);
            atom.setProperty("Name", atomName);
            atom.setProperty("Coords", coordinates);
            residue.addSubStructure(atom);
	        
	        return chain;
	    }
	    
	    public static Structure createResidue(String residueNumber, String residueName, int chainIndex) {
//            System.out.println("residue " + residueNumber + " " + residueName + " " + chainIndex);
	    	Structure residue = new Structure(Level.RESIDUE);
            residue.setProperty("Index", String.valueOf(chainIndex));
	    	residue.setProperty("Number", residueNumber);
	    	residue.setProperty("Name", residueName);
	    	return residue;
	    }
}
