package aigen.results;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tailor.datasource.aigen.Feature;
import tailor.datasource.aigen.PDBFileList;
import tailor.datasource.aigen.ResidueID;
import tailor.structure.Atom;
import tailor.structure.Chain;
import tailor.structure.ChainType;
import tailor.structure.Group;
import tailor.structure.Protein;
import tailor.structure.Structure;

/**
* Streams examples from PDB files
*/
class ExampleStream {
   private PDBFileList pdbFileList;
   
   public ExampleStream(String pdbdirectory) throws IOException {
       this.pdbFileList = new PDBFileList(pdbdirectory);
   }
   
   public Iterator<Structure> examples(String pdbid, List<ExampleDescription> exampleDescriptions) {
       return new Iterator<Structure>() {
           private Structure structure;
           private Iterator<ExampleDescription> descIterator;
           private Structure nextExample;
           
           {
               try {
                   structure = pdbFileList.get(pdbid);
                   descIterator = exampleDescriptions.iterator();
                   advance();
               } catch (Exception e) {
                   System.err.println(e.getMessage());
                   descIterator = new ArrayList<ExampleDescription>().iterator();
               }
           }
           
           private void advance() {
               nextExample = null;
               while (descIterator.hasNext() && nextExample == null) {
                   try {
                       ExampleDescription desc = descIterator.next();
//                       nextExample = desc.findIn(structure);	XXX
                   } catch (Exception e) {
                       System.err.println(e.getMessage());
                   }
               }
           }
           
           @Override
           public boolean hasNext() {
               return nextExample != null;
           }
           
           @Override
           public Structure next() {
               Structure result = nextExample;
               advance();
               return result;
           }
       };
   }
}

/**
* Description of an example to extract from a PDB structure
*/
class ExampleDescription {
   private String pdbid;
   private String chainName;
   private int residueStart;
   private int residueEnd;
   private int ligandNum;
   
   public ExampleDescription(String pdbid, String chainName, String residueStart, 
                           String residueEnd, String ligandNum) {
       this.pdbid = pdbid;
       this.chainName = chainName;
       this.residueStart = Integer.parseInt(residueStart);
       this.residueEnd = Integer.parseInt(residueEnd);
       this.ligandNum = Integer.parseInt(ligandNum);
   }
   
   public String getPdbid() {
       return pdbid;
   }
   
   public String getChain() {
       return chainName;
   }
   
   public int getResidueStart() {
       return residueStart;
   }
   
   public int getResidueEnd() {
       return residueEnd;
   }
   
   public int getLigandNum() {
       return ligandNum;
   }
   
   /**
    * Finds and extracts this example from the given structure
    */
   public Structure findIn(Protein structure) {
       Structure example = new Protein(this.pdbid);
       
       // Get the first model
       // TODO
//       Model sourceModel = (Model)structure.get(0);
//       Model modelFeature = new Model(0);
//       example.add(modelFeature);
       
       // Process chains
       for (Chain chain : structure.getChains()) {
    	   
           if (chain.getName().equals(this.chainName) || this.chainName.equals(" ")) {
               Chain chainFeature = new Chain(chain.getName(), ChainType.PEPTIDE);
               
               // Process residues within the range
               for (Group residue : chain.getGroups()) {
                   int resNum = residue.getNumber();
                   if (this.residueStart <= resNum && resNum <= this.residueEnd) {
                	   ResidueID residueID = new ResidueID(residue.getNumber(), "");
                       Group residueFeature = new Group(residueID, residue.getName());
                       
                       // Copy atoms
                       for (Atom atom : residue.getAtoms()) {
                           residueFeature.addAtom(atom.copy());
                       }
                       
                       chainFeature.addGroup(residueFeature);
                   }
               }
               
               // TODO
//               modelFeature.add(chainFeature);
           }
       }
       
       // Add the ligand (water molecule)
       Chain waterChain = new Chain("Water", ChainType.WATER);
       ResidueID residueID = new ResidueID(ligandNum, "");
       Group waterResidue = new Group(residueID, "HOH");
       
       List<Chain> waterChains = structure.chainsOfType(ChainType.WATER);
       if (!waterChains.isEmpty()) {
           Group existingWater = waterChains.get(0).getGroupAt(this.ligandNum);
           if (existingWater != null) {
               Atom oAtom = existingWater.getAtom("O");
               if (oAtom != null) {
                   waterResidue.addAtom(oAtom.copy());
               }
           }
       }
       
       waterChain.addGroup(waterResidue);
       
       // TODO
//       modelFeature.add(waterChain);
       
       return example;
   }
   
   @Override
   public String toString() {
       return String.format("%s.%s %d-%d %d", 
           pdbid, chainName, residueStart, residueEnd, ligandNum);
   }
}
