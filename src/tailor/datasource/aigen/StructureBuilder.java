package tailor.datasource.aigen;

import tailor.structure.Protein;
import tops.translation.model.Atom;
import tops.translation.model.Chain;
import tops.translation.model.Group;
import tops.translation.model.PolymerType;

public class StructureBuilder {
    private Protein structure;
    
    private Chain chain;
    private Group residue;
    
    // TODO
//    private Model model;
    private Integer modelID;
    
    private String chainID;
    private PolymerType chainType;
    private String segID;
    private Integer residueNumber;
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
        this.residueNumber = null;
        this.resname = null;
    }
    
    public Protein getStructure() {
        Protein s = this.structure;
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
    
    public void initChain(String chainID, PolymerType chainType) {
        this.chainID = chainID;
        this.chainType = chainType;
        this.chain = new Chain(chainID, chainType);
//        this.model.add(this.chain);
        this.structure.addChain(chain);
    }
    
    public void registerLine(String resname, int resseq, 
                            String icode, String segID, String chainID, PolymerType chainType) {
        if (!chainID.equals(this.chainID) || !chainType.equals(this.chainType)) {
            initChain(chainID, chainType);
            initResidue(resname, resseq, icode);
        } else if (resseq != this.residueNumber || !resname.equals(this.resname)) {
            initResidue(resname, resseq, icode);
        }
        
        if (!segID.equals(this.segID)) {
            this.segID = segID;
        }
    }
    
    
    public void initResidue(String resname, int resnum, String icode) {
        this.resname = resname;
        this.residue = new Group(resnum, icode, resname, this.segID);
        this.chain.addGroup(this.residue);
    }
    
    public void initAtom(String name, double[] coord, double bFactor, 
                        double occupancy, String altloc) {
        Atom atom = new Atom(name, coord, bFactor, occupancy, altloc);
        this.residue.addAtom(atom);
    }
}
