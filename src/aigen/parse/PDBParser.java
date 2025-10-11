package aigen.parse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class PDBParser {
    private StructureBuilder builder;
    
    public PDBParser(StructureBuilder builder) {
        this.builder = builder;
    }
    
    public aigen.feature.Structure getStructure(String path) throws IOException {
        String pdbID = PDBUtils.getPDBIDFromPath(path);
        builder.initStructure(pdbID);
        
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        int lineNumber = 0;
        
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            
            // Ensure line is long enough
            if (line.length() < 6) continue;
            
            String recordType = line.substring(0, 6);
            
            if (recordType.equals("ATOM  ") || recordType.equals("HETATM")) {
                // Parse atom/hetatm line
                String name = safeSubstring(line, 12, 16);
                String altloc = safeSubstring(line, 16, 17);
                String resname = safeSubstring(line, 17, 20).trim();
                String chainID = safeSubstring(line, 21, 22);
                int resseq = Integer.parseInt(safeSubstring(line, 22, 26).trim());
                String icode = safeSubstring(line, 26, 27);
                
                ResidueID residueID = new ResidueID(resseq, icode);
                
                // Atomic coordinates
                double x = Double.parseDouble(safeSubstring(line, 30, 38).trim());
                double y = Double.parseDouble(safeSubstring(line, 38, 46).trim());
                double z = Double.parseDouble(safeSubstring(line, 46, 54).trim());
                double[] coord = {x, y, z};
                
                // Occupancy & B factor
                double occupancy = Double.parseDouble(safeSubstring(line, 54, 60).trim());
                double bfactor = Double.parseDouble(safeSubstring(line, 60, 66).trim());
                
                String segID = safeSubstring(line, 72, 76).trim();
                
                String chainType;
                if (recordType.equals("HETATM")) {
                    if (resname.equals("HOH") || resname.equals("DOD")) {
                        chainID = "Water";
                        chainType = "Water";
                    } else {
                        chainID = "Ligand";
                        chainType = "Ligand";
                    }
                } else {
                    chainType = "Protein";
                }
                
                builder.registerLine(residueID, resname, resseq, icode, segID, chainID, chainType);
                builder.initAtom(name, coord, bfactor, occupancy, altloc);
                
            } else if (recordType.equals("ENDMDL")) {
                builder.initModel();
                
            } else if (recordType.equals("END   ") || recordType.equals("CONECT")) {
                break;
            }
        }
        
        reader.close();
        return builder.getStructure();
    }
    
    // Helper method to safely extract substring
    private String safeSubstring(String str, int start, int end) {
        if (str.length() < start) return "";
        if (str.length() < end) return str.substring(start);
        return str.substring(start, end);
    }
    
    public static void main(String[] args) {
//      if (args.length < 1) {
//          System.err.println("Usage: java PDBParserMain <pdb_file>");
//          System.exit(1);
//      }
//      
//      try {
//          PDBParser parser = new PDBParser(new StructureBuilder());
//          Structure structure = parser.getStructure(args[0]);
//          
//          System.out.println(structure + " has " + structure.size() + " models");
//          
////          for (Model model : structure) {
////              System.out.println(model + " has " + model.size() + " chains");
////              
////              for (Chain chain : model) {
////                  System.out.println(chain + " has " + chain.size() + " residues");
////                  
////                  for (Residue residue : chain) {
////                      System.out.println(residue + " has " + residue.size() + " atoms");
////                  }
////              }
////          }
//      } catch (IOException e) {
//          System.err.println("Error reading PDB file: " + e.getMessage());
//          e.printStackTrace();
//      }
  }
}
