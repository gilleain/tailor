package tailor.datasource.aigen;

import tailor.structure.Atom;
import tailor.structure.Chain;
import tailor.structure.ChainType;
import tailor.structure.Group;
import tailor.structure.Protein;
import tailor.structure.Structure;

public class StructureBuilder {
    private Structure structure;
    
    private Chain chain;
    private Group residue;
    
    // TODO
//    private Model model;
    private Integer modelID;
    
    private String chainID;
    private ChainType chainType;
    private String segID;
    private ResidueID residueID;
    private String resname;
    
    public StructureBuilder() {
        reset();
    }
    
    public void reset() {
        this.structure = null;
//        this.model = null;
        this.chain = null;
        this.residue = null;
        
        this.modelID = null;
        this.chainID = null;
        this.chainType = null;
        this.segID = null;
        this.residueID = null;
        this.resname = null;
    }
    
    public Structure getStructure() {
        Structure s = this.structure;
        reset();
        return s;
    }
    
    public void initStructure(String structureID) {
        this.structure = new Protein(structureID);
//        initModel();
    }
    
    // TODO - no such thing as 'Model' in hierarchy ...
//    public void initModel() {
//        if (this.modelID == null) {
//            this.modelID = 0;
//        } else {
//            this.modelID++;
//        }
//        this.model = new Model(this.modelID);
//        this.structure.add(this.model);
//        this.chainID = null;
//        this.chainType = null;
//    }
    
    public void initChain(String chainID, ChainType chainType) {
        this.chainID = chainID;
        this.chainType = chainType;
        this.chain = new Chain(chainID, chainType);
//        this.model.add(this.chain);
        this.structure.addSubStructure(chain);
    }
    
    public void registerLine(ResidueID residueID, String resname, int resseq, 
                            String icode, String segID, String chainID, ChainType chainType) {
        if (!chainID.equals(this.chainID) || !chainType.equals(this.chainType)) {
            initChain(chainID, chainType);
            initResidue(residueID, resname, resseq, icode);
        } else if (!residueID.equals(this.residueID) || !resname.equals(this.resname)) {
            initResidue(residueID, resname, resseq, icode);
        }
        
        if (!segID.equals(this.segID)) {
            this.segID = segID;
        }
    }
    
    
    public void initResidue(ResidueID residueID, String resname, int resnum, String icode) {
        this.residueID = residueID;
        this.resname = resname;
        this.residue = new Group(residueID, resname, this.segID);
        this.chain.addSubStructure(this.residue);
    }
    
    public void initAtom(String name, double[] coord, double bFactor, 
                        double occupancy, String altloc) {
        Atom atom = new Atom(name, coord, bFactor, occupancy, altloc);
        this.residue.addSubStructure(atom);
    }
}
