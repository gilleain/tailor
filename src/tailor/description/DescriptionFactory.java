package tailor.description;

import java.util.ArrayList;

import tailor.condition.HBondCondition;
import tailor.condition.TorsionBoundCondition;
import tailor.measure.TorsionMeasure;
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
	
	public void createHBondCondition(double maxDH, double minDHA, double minHAA,
	        int donorNumber, int acceptorNumber) {
		this.addHBondCondition(
		        new HBondCondition(maxDH, minDHA, minHAA),
		        donorNumber, acceptorNumber);
	}
	
	public void addHBondCondition(HBondCondition partialCondition, 
	        int donorNumber, int acceptorNumber) {
		this.addHBondConditionToChain(partialCondition, donorNumber, 
				acceptorNumber, DescriptionFactory.DEFAULT_CHAIN_NAME);
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
	
	public ArrayList<AtomDescription> getAtomDescriptions(String chainName) {
		ArrayList<AtomDescription> atoms = new ArrayList<AtomDescription>();
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
	public void addHBondConditionToChain(HBondCondition partialCondition, 
	        int donorNumber, int acceptorNumber, String chainName) {
		
		ChainDescription chain = this.root.getChainDescription(chainName);
		
		ChainDescription a = chain.getPath(donorNumber, "N");
		ChainDescription b = chain.getPath(donorNumber, "H");
		ChainDescription c = chain.getPath(acceptorNumber, "O");
		ChainDescription d = chain.getPath(acceptorNumber, "C");
		
		partialCondition.setDonorAtomDescription(a);
		partialCondition.setHydrogenAtomDescription(b);
		partialCondition.setAcceptorAtomDescription(c);
		partialCondition.setAttachedAtomDescription(d);
		
		chain.addCondition(partialCondition);
	}
	
	public void addPhiConditionToChain(TorsionBoundCondition partialCondition, 
	        int residueNumber, String chainName) {
		ChainDescription chain = this.root.getChainDescription(chainName);
		
		ChainDescription a = chain.getPath(residueNumber - 1, "C");
		ChainDescription b = chain.getPath(residueNumber, "N");
		ChainDescription c = chain.getPath(residueNumber, "CA");
		ChainDescription d = chain.getPath(residueNumber, "C");
		
		partialCondition.setDescriptionA(a);
		partialCondition.setDescriptionB(b);
		partialCondition.setDescriptionC(c);
		partialCondition.setDescriptionD(d);
		
		chain.addCondition(partialCondition);
	}
	
	public void createPhiCondition(
	        double midPoint, double range, int residueNumber) {
		TorsionBoundCondition partialCondition = 
		    new TorsionBoundCondition("phi", midPoint, range);
		this.addPhiConditionToChain(partialCondition, 
				residueNumber, DescriptionFactory.DEFAULT_CHAIN_NAME);
	}
	
	public void addPsiConditionToChain(TorsionBoundCondition partialCondition, 
	        int residueNumber, String chainName) {
		ChainDescription chain = root.getChainDescription(chainName);
		
		ChainDescription a = chain.getPath(residueNumber, "N");
		ChainDescription b = chain.getPath(residueNumber, "CA");
		ChainDescription c = chain.getPath(residueNumber, "C");
		ChainDescription d = chain.getPath(residueNumber + 1, "N");
		
		partialCondition.setDescriptionA(a);
		partialCondition.setDescriptionB(b);
		partialCondition.setDescriptionC(c);
		partialCondition.setDescriptionD(d);
		
		chain.addCondition(partialCondition);
	}
	
	public void createPsiCondition(
	        double midPoint, double range, int residueNumber) {
		TorsionBoundCondition partialCondition = 
		    new TorsionBoundCondition("phi", midPoint, range);
		this.addPsiConditionToChain(partialCondition, 
				residueNumber, DescriptionFactory.DEFAULT_CHAIN_NAME);
	}
	
	public TorsionMeasure createPhiMeasure(
	        String name, int residueNumber, String chainName) {
		return fillPhiMeasure(new TorsionMeasure(name), residueNumber, chainName);
	}
	
	public TorsionMeasure fillPhiMeasure(
	       TorsionMeasure partialMeasure, int residueNumber, String chainName) {
		ProteinDescription a = root.getPath(chainName, residueNumber - 1, "C");
		ProteinDescription b = root.getPath(chainName, residueNumber, "N");
		ProteinDescription d = root.getPath(chainName, residueNumber, "C");
		ProteinDescription c = root.getPath(chainName, residueNumber, "CA");
		
		partialMeasure.setDescriptionA(a);
		partialMeasure.setDescriptionB(b);
		partialMeasure.setDescriptionC(c);
		partialMeasure.setDescriptionD(d);
		
		return partialMeasure;
	}
	
	public TorsionMeasure createPsiMeasure(
	        String name, int residueNumber, String chainName) {
		return fillPsiMeasure(
		        new TorsionMeasure(name), residueNumber, chainName);
	}
	
	public TorsionMeasure fillPsiMeasure(
	       TorsionMeasure partialMeasure, int residueNumber, String chainName) {
		ProteinDescription a = root.getPath(chainName, residueNumber, "N");
		ProteinDescription b = root.getPath(chainName, residueNumber, "CA");
		ProteinDescription c = root.getPath(chainName, residueNumber, "C");
		ProteinDescription d = root.getPath(chainName, residueNumber + 1, "N");
		
		partialMeasure.setDescriptionA(a);
		partialMeasure.setDescriptionB(b);
		partialMeasure.setDescriptionC(c);
		partialMeasure.setDescriptionD(d);
		
		return partialMeasure;
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
    
}
