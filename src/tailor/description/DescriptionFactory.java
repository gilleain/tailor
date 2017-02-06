package tailor.description;

import java.util.ArrayList;
import java.util.List;

import tailor.condition.HBondCondition;
import tailor.condition.TorsionBoundCondition;
import tailor.measurement.HBondMeasure;
import tailor.measurement.TorsionMeasure;
import tailor.structure.Level;


/**
 * A class used to construct Descriptions. 
 * The name 'Factory' is not meant to suggest use of the Abstract Factory pattern.
 * 
 * @author maclean
 *
 */
public class DescriptionFactory {
    
    /**
     * If true, each ResidueDescription will be created with an AtomDescription
     * of 'H' (the backbone N-H)
     */
    private boolean addBackboneAmineHydrogens;
	
	private static String DEFAULT_CHAIN_NAME = "A";
	
	private ProteinDescription root;
	
	public DescriptionFactory() {
		this("Motif");				// The default name. 
	}
	
	public DescriptionFactory(String name) {
		this.root = new ProteinDescription(name);
	}
	
	public void setDescription(ProteinDescription root) {
		this.root = root;
	}
	
	public void setAddBackboneAmineHydrogens(boolean value) {
	    this.addBackboneAmineHydrogens = value;
	}
	
	public int lookupID(int groupIndex, String atomName) {
	    // XXX assumes that there is only one chain
	    ChainDescription chainD = root.getChainDescription(DEFAULT_CHAIN_NAME);
	    GroupDescription groupD = chainD.getGroupDescription(groupIndex);
	    AtomDescription  atomD  = groupD.getAtomDescription(atomName);
	    return atomD.getID();
	}
	
	public AtomDescription lookup(Description path) {
		return this.lookup(path, this.root);
	}
	
	public AtomDescription lookup(Description path, Description self) {
		Level level = path.getLevel();
		
		if (level == Level.CHAIN) {
			ChainDescription pathChain = (ChainDescription) path;
			GroupDescription pathGroup = pathChain.getGroupDescriptions().get(0);
			String name = pathChain.getName();
			self = ((ProteinDescription) self).getChainDescription(name);
			return this.lookup(pathGroup, self);
		} else if (level == Level.RESIDUE) {
			GroupDescription pathGroup = (GroupDescription) path;
			AtomDescription pathAtom = pathGroup.getAtomDescriptions().get(0);
//			int offset = pathGroup.getOffset();
			// TODO : FIXME
//			self = ((ChainDescription) self).getGroupDescription(offset);
			self = ((ChainDescription) self).getGroupDescription(0);
			return this.lookup(pathAtom, self);
		} else if (level == Level.ATOM) {
			AtomDescription pathAtom = (AtomDescription) path;
			String name = pathAtom.getName();
			return ((GroupDescription) self).getAtomDescription(name);
		} else {
			return null;	// XXX error
		}
	}
	
	public void removeLastResidueFromChain(String chainName) {
		ChainDescription chain = this.root.getChainDescription(chainName);
		if (chain != null) {
			chain.removeLastGroupDescription();
		}
	}
	
	public void addChainToProtein() {
		this.addChainToProtein(DescriptionFactory.DEFAULT_CHAIN_NAME);
	}
	
	public void addResidues(int numberOfResidues) {
		if (!this.root.hasChain(DescriptionFactory.DEFAULT_CHAIN_NAME)) {
			this.addChainToProtein();
		}
		this.addMultipleResiduesToChain(
		        DescriptionFactory.DEFAULT_CHAIN_NAME, numberOfResidues);
	}
	
	public HBondCondition createHBondCondition(
	        double maxDH, double minDHA, double minHAA, int donorNumber, int acceptorNumber) {
	    ChainDescription chain = this.root.getChainDescription(DescriptionFactory.DEFAULT_CHAIN_NAME);
        
        ChainDescription a = chain.getPath(donorNumber, "N");
        ChainDescription b = chain.getPath(donorNumber, "H");
        ChainDescription c = chain.getPath(acceptorNumber, "O");
        ChainDescription d = chain.getPath(acceptorNumber, "C");
        return new HBondCondition(a, b, c, d, maxDH, minDHA, minHAA);
	}
	
	public void addHBondCondition(double maxDH, double minDHA, double minHAA,
	        int donorNumber, int acceptorNumber) {
		this.addHBondConditionToChain(
		        maxDH, minDHA, minHAA, donorNumber, acceptorNumber, DescriptionFactory.DEFAULT_CHAIN_NAME);
	}
	
	public TorsionMeasure createPhiMeasure(String name, int residueNumber) {
		return this.createPhiMeasure(name, residueNumber,
		        DescriptionFactory.DEFAULT_CHAIN_NAME);
	}
	
	public TorsionMeasure createPsiMeasure(String name, int residueNumber) {
		return this.createPsiMeasure(name, residueNumber,
		        DescriptionFactory.DEFAULT_CHAIN_NAME);
	}
	
	public boolean canHydrogenBond(AtomDescription a, AtomDescription b) {
		String aName = a.getName();
		String bName = b.getName();
		
		return (aName.equals("O") && bName.equals("N")) 
		    || (aName.equals("N") && bName.equals("O"));
	}
	
	public ChainDescription getChainDescription(String chainName) {
		return this.root.getChainDescription(chainName);
	}
	
	public GroupDescription getGroupDescription(String chainName, int position) {
		ChainDescription chain = this.root.getChainDescription(chainName);
		if (chain != null) {
			return chain.getGroupDescription(position);
		} else {
			return null;	// TODO : raise exception
		}
	}
	
	public List<AtomDescription> getAtomDescriptions(String chainName) {
		List<AtomDescription> atoms = new ArrayList<AtomDescription>();
		ChainDescription chain = this.root.getChainDescription(chainName);
		for (GroupDescription group : chain.getGroupDescriptions()) {
			atoms.addAll(group.getAtomDescriptions());
		}
		return atoms;
	}
	
	public ProteinDescription getProduct() {
		return this.root;
	}
	
	public void addChainToProtein(String name) {
		this.root.addChainDescription(new ChainDescription(name));
	}
	
	public void addResidueToChain(String chainName) {
		ChainDescription chain = this.root.getChainDescription(chainName);
		if (chain != null) {
			this.addResidue(chain, new GroupDescription());
		}
	}
	
	public void addResidueToChain(String chainName, String residueName) {
		ChainDescription chain = this.root.getChainDescription(chainName);
		if (chain != null) {
			this.addResidue(chain, new GroupDescription(residueName));
		}
	}
	
	public void addMultipleResiduesToChain(String chainName, int numberOfResidues) {
		ChainDescription chain = this.root.getChainDescription(chainName);
		if (chain != null) {
			for (int i = 0; i < numberOfResidues; i++) {
				// XXX ACK! - overwrites 0 to numberOfResidues...
				this.addResidue(chain, new GroupDescription());
			}
		}
	}
	
	private void addResidue(ChainDescription chain, GroupDescription residue) {
	    chain.addGroupDescription(residue);
		residue.addAtomDescription("N");
		if (addBackboneAmineHydrogens) {
		    residue.addAtomDescription("H");
		}
		residue.addAtomDescription("CA");
		residue.addAtomDescription("C");
		residue.addAtomDescription("O");
	}
	
	public void addAtomToResidue(
	        String chainName, int residueNumber, String atomName) {
		ChainDescription chain = this.root.getChainDescription(chainName);
		if (chain != null) {
			GroupDescription residue = chain.getGroupDescription(residueNumber);
			residue.addAtomDescription(new AtomDescription(atomName));
		}
	}
	
	/**
	 * @param partialCondition An HBondCondition with only the values filled.
	 * @param donorNumber The residue number of the lower numbered end.
	 * @param acceptorNumber The residue number of the higher numbered end.
	 * @param chainName The name of the chain to create the condition on.
	 */
	public void addHBondConditionToChain(
	        double maxDH, double minDHA, double minHAA, 
	        int donorNumber, int acceptorNumber, String chainName) {
	    addHBondConditionToChain(
	            createHBondCondition(maxDH, minDHA, minHAA, donorNumber, acceptorNumber), chainName);
	}
	
	public void addHBondConditionToChain(HBondCondition hBondCondition, String chainName) {
	    ChainDescription chain = this.root.getChainDescription(chainName);
        chain.addCondition(hBondCondition);   
	}
	
	public void addPhiConditionToChain(
	        TorsionBoundCondition partialCondition, String chainName) {
		
	    ChainDescription chain = this.root.getChainDescription(chainName);
		chain.addCondition(partialCondition);
	}
	
	public TorsionBoundCondition createPhiCondition(
	        double midPoint, double range, int residueNumber, String chainName) {
	    
	    ChainDescription chain = this.root.getChainDescription(chainName);
        
        ChainDescription a = chain.getPath(residueNumber - 1, "C");
        ChainDescription b = chain.getPath(residueNumber, "N");
        ChainDescription c = chain.getPath(residueNumber, "CA");
        ChainDescription d = chain.getPath(residueNumber, "C");
        
		return new TorsionBoundCondition("phi", a, b, c, d, midPoint, range);
	}
	
	public void addPsiConditionToChain(
	        TorsionBoundCondition partialCondition, String chainName) {
	    ChainDescription chain = root.getChainDescription(chainName);
		chain.addCondition(partialCondition);
	}
	
	public TorsionBoundCondition createPsiCondition(
	        double midPoint, double range, int residueNumber, String chainName) {
	    ChainDescription chain = root.getChainDescription(chainName);
        
        ChainDescription a = chain.getPath(residueNumber, "N");
        ChainDescription b = chain.getPath(residueNumber, "CA");
        ChainDescription c = chain.getPath(residueNumber, "C");
        ChainDescription d = chain.getPath(residueNumber + 1, "N");
	    
		return new TorsionBoundCondition("phi", a, b, c, d, midPoint, range);
	}
	
	public TorsionMeasure createPhiMeasure(
	        String name, int residueNumber, String chainName) {
	    ProteinDescription a = root.getPath(chainName, residueNumber - 1, "C");
        ProteinDescription b = root.getPath(chainName, residueNumber, "N");
        ProteinDescription d = root.getPath(chainName, residueNumber, "C");
        ProteinDescription c = root.getPath(chainName, residueNumber, "CA");
	    
		return new TorsionMeasure(name, a, b, c, d);
	}
	
	public void addTorsionMeasureToChain(TorsionMeasure torsionMeasure, String chainName) {
	    ChainDescription chain = this.root.getChainDescription(chainName);
	    chain.addMeasure(torsionMeasure);
	}
	
	public TorsionMeasure createPsiMeasure(String name, int residueNumber, String chainName) {
	    ProteinDescription a = root.getPath(chainName, residueNumber, "N");
        ProteinDescription b = root.getPath(chainName, residueNumber, "CA");
        ProteinDescription c = root.getPath(chainName, residueNumber, "C");
        ProteinDescription d = root.getPath(chainName, residueNumber + 1, "N");
	    
		return new TorsionMeasure(name, a, b, c, d);
	}
    
    public static Description createFromLevel(Level level, String name) {
        switch (level) {
            case PROTEIN : return new ProteinDescription(name);
            case CHAIN   : return new ChainDescription(name);
            case RESIDUE : return new GroupDescription(name);
            case ATOM    : return new AtomDescription(name);
            default      : return null;
        }
    }
    
    public static Level getSubLevel(Level level) {
        Level[] levels = Level.values();
        for (int i = 0; i < levels.length - 1; i++) {
            if (levels[i] == level) {
                return levels[i + 1];
            }
        }
        return Level.UNKNOWN;
    }

    public HBondMeasure createHBondMeasure(int donorNumber, int acceptorNumber) {
        ChainDescription chain = this.root.getChainDescription(DescriptionFactory.DEFAULT_CHAIN_NAME);
        
        ChainDescription a = chain.getPath(donorNumber, "N");
        ChainDescription b = chain.getPath(donorNumber, "H");
        ChainDescription c = chain.getPath(acceptorNumber, "O");
        ChainDescription d = chain.getPath(acceptorNumber, "C");
        
        return new HBondMeasure("HBond", a, b, c, d);
    }
    
}
