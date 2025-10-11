package aigen.parse;


import aigen.feature.Atom;
import aigen.feature.Chain;
import aigen.feature.Model;
import aigen.feature.Residue;
import aigen.feature.Structure;

class StructureBuilder {
    private Structure structure;
    private Model model;
    private Chain chain;
    private Residue residue;
    
    private Integer modelID;
    private String chainID;
    private String chainType;
    private String segID;
    private ResidueID residueID;
    private String resname;
    
    public StructureBuilder() {
        reset();
    }
    
    public void reset() {
        this.structure = null;
        this.model = null;
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
        this.structure = new Structure(structureID);
        initModel();
    }
    
    public void initModel() {
        if (this.modelID == null) {
            this.modelID = 0;
        } else {
            this.modelID++;
        }
        this.model = new Model(this.modelID);
        this.structure.add(this.model);
        this.chainID = null;
        this.chainType = null;
    }
    
    public void initChain(String chainID, String chainType) {
        this.chainID = chainID;
        this.chainType = chainType;
        this.chain = new Chain(chainID, chainType);
        this.model.add(this.chain);
    }
    
    public void registerLine(ResidueID residueID, String resname, int resseq, 
                            String icode, String segID, String chainID, String chainType) {
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
        this.residue = new Residue(residueID, resname, this.segID);
        this.chain.add(this.residue);
    }
    
    public void initAtom(String name, double[] coord, double bFactor, 
                        double occupancy, String altloc) {
        Atom atom = new Atom(name, coord, bFactor, occupancy, altloc);
        this.residue.add(atom);
    }
}
