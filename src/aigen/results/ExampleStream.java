package aigen.results;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import aigen.feature.Atom;
import aigen.feature.Chain;
import aigen.feature.Feature;
import aigen.feature.Model;
import aigen.feature.Residue;
import aigen.feature.Structure;
import tailor.datasource.aigen.PDBFileList;

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
           private tailor.structure.Structure structure;
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
   private String chain;
   private int residueStart;
   private int residueEnd;
   private int ligandNum;
   
   public ExampleDescription(String pdbid, String chain, String residueStart, 
                           String residueEnd, String ligandNum) {
       this.pdbid = pdbid;
       this.chain = chain;
       this.residueStart = Integer.parseInt(residueStart);
       this.residueEnd = Integer.parseInt(residueEnd);
       this.ligandNum = Integer.parseInt(ligandNum);
   }
   
   public String getPdbid() {
       return pdbid;
   }
   
   public String getChain() {
       return chain;
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
   public Structure findIn(Structure structure) {
       Structure example = new Structure(this.pdbid);
       
       // Get the first model
       Model sourceModel = (Model)structure.get(0);
       Model modelFeature = new Model(0);
       example.add(modelFeature);
       
       // Process chains
       for (Feature chainSubFeature : sourceModel) {
    	   Chain chain = (Chain) chainSubFeature;
           if (chain.getChainID().equals(this.chain) || this.chain.equals(" ")) {
               Chain chainFeature = new Chain(chain.getChainID(), "Protein");
               
               // Process residues within the range
               for (Feature residueSubFeature : chain) {
            	   Residue residue = (Residue) residueSubFeature;
                   int resNum = residue.getNumber();
                   if (this.residueStart <= resNum && resNum <= this.residueEnd) {
                       Residue residueFeature = new Residue(residue.getNumber(), residue.getResname());
                       
                       // Copy atoms
                       for (Feature atomSubFeature : residue) {
                    	   Atom atom = (Atom) atomSubFeature;
                           residueFeature.add(atom.copy());
                       }
                       
                       chainFeature.add(residueFeature);
                   }
               }
               
               modelFeature.add(chainFeature);
           }
       }
       
       // Add the ligand (water molecule)
       Chain waterChain = new Chain("Water", "Water");
       Residue waterResidue = new Residue(String.valueOf(this.ligandNum), "HOH");
       
       List<Chain> waterChains = structure.chainsOfType("Water");
       if (!waterChains.isEmpty()) {
           Residue existingWater = waterChains.get(0).getResidueNumber(this.ligandNum);
           if (existingWater != null) {
               Atom oAtom = existingWater.getAtom("O");
               if (oAtom != null) {
                   waterResidue.add(oAtom.copy());
               }
           }
       }
       
       waterChain.add(waterResidue);
       modelFeature.add(waterChain);
       
       return example;
   }
   
   @Override
   public String toString() {
       return String.format("%s.%s %d-%d %d", 
           pdbid, chain, residueStart, residueEnd, ligandNum);
   }
}
