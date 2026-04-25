package tailor.description;

import static tailor.description.DescriptionPath.getPath;

import java.util.Arrays;
import java.util.List;

import tailor.description.atom.AtomTorsionRangeDescription;
import tailor.description.atom.HBondDescription;
import tailor.description.group.GroupSequenceDescription;
import tailor.measure.AtomTorsionMeasure;
import tailor.measure.HBondMeasure;


/**
 * Construct descriptions and measures.
 */
public class DescriptionFactory {
	
	private ChainDescription chain;	// TODO - should be a collection of these
	
	/**
     * If true, each ResidueDescription will be created with an AtomDescription
     * of 'H' (the backbone N-H)
     */
    private boolean addBackboneAmineHydrogens;
	
	public DescriptionFactory(String chainName) {
		this.chain = new ChainDescription(chainName);
	}
	
	public GroupDescription addResidueToChain(String chainLabel) {
		return addResidueToChain(chainLabel, null);
	}

	public GroupDescription addResidueToChain(String chainLabel, String groupLabel) {
		return addResidueToChain(getChainDescription(chainLabel), groupLabel);
	}
	
	public GroupDescription addResidueToChain(ChainDescription chainToAddTo, String groupLabel) {	
		// TODO - select chain
		if (groupLabel == null) {
			return addResidue(chainToAddTo, new GroupDescription());
		} else {
			return addResidue(chainToAddTo, new GroupDescription(groupLabel));
		}
	}
	
	private GroupDescription addResidue(ChainDescription chain, GroupDescription residue) {
	    chain.addGroupDescription(residue);
		residue.addAtomDescription("N");
		if (addBackboneAmineHydrogens) {
		    residue.addAtomDescription("H");
		}
		residue.addAtomDescription("CA");
		residue.addAtomDescription("C");
		residue.addAtomDescription("O");
		return residue;
	}

	public void addResiduesAsSegment(int numberToAdd) {
		String chainLabel = "A";
		ChainDescription chainToAddTo = getChainDescription(chainLabel);
		GroupDescription prevGroup = null;
		for (int index = 0; index < numberToAdd; index++) {
			GroupDescription group = addResidueToChain(chainToAddTo, "");
			if (prevGroup != null) {
				chainToAddTo.addGroupSequenceDescriptions(new GroupSequenceDescription(prevGroup, group, 1));
			}
			prevGroup = group;
		}
	}
	
	public void addResidues(int numberToAdd) {
		addMultipleResiduesToChain("A", numberToAdd);
	}

	public void addMultipleResiduesToChain(String chainLabel, int numberToAdd) {
		for (int index = 0; index < numberToAdd; index++) {
			addResidueToChain(chainLabel, "");
		}
	}

	public ChainDescription getChainDescription(String string) {
		return chain;	// TODO
	}

	public void setAddBackboneAmineHydrogens(boolean add) {
		this.addBackboneAmineHydrogens = add;
	}

	public static class GroupBuilder {
		private String groupLabel = null;
		private List<String> atomLabels;
		
		public GroupBuilder withGroupLabel(String label) {
			this.groupLabel = label;
			return this;
		}
		
		public GroupBuilder withAtomLabels(String... labels) {
			this.atomLabels = Arrays.asList(labels);
			return this;
		}
		
		public GroupDescription build() {
			GroupDescription groupDescription = new GroupDescription(groupLabel);
			for (String atomLabel : atomLabels) {
				groupDescription.addAtomDescription(new AtomDescription(atomLabel));
			}
			return groupDescription;
		}
		
	}
	
	public static GroupBuilder group() {
		return new GroupBuilder();
	}
	
	public static class MeasureBuilder {
		
		private ChainDescription chain;
		
		public MeasureBuilder(ChainDescription chain) {
			this.chain = chain;
		}
		
		private ChainDescription getChainDescription(String chainName) {
			return this.chain;	// TODO
		}

		public AtomTorsionMeasure createPhiMeasure(String chainName, int groupNumber, String measureName) {
			return createPhi(getChainDescription(chainName), groupNumber, measureName);
		}

		public AtomTorsionMeasure createPsiMeasure(String chainName, int groupNumber, String measureName) {
			return createPhi(getChainDescription(chainName), groupNumber, measureName);
		}

		public AtomTorsionMeasure createPhiMeasure(String chainName, int groupNumber) {
			return createPhi(getChainDescription(chainName), groupNumber, "phi");
		}

		public AtomTorsionMeasure createPsiMeasure(String chainName, int groupNumber) {
			return createPhi(getChainDescription(chainName), groupNumber, "psi");
		}

		public HBondMeasure createHBondMeasure(String chainName, int donorNumber, int acceptorNumber) {
			ChainDescription currentChain = getChainDescription(chainName);

			DescriptionPath a = getPath(currentChain, donorNumber, "N");
			DescriptionPath b = getPath(currentChain, donorNumber, "H");
			DescriptionPath c = getPath(currentChain, acceptorNumber, "O");
			DescriptionPath d = getPath(currentChain, acceptorNumber, "C");	// TODO

			return new HBondMeasure("HBond", List.of(b, c), List.of( a, b, c));
		}
		
		private AtomTorsionMeasure createPhi(ChainDescription chain, int residueNumber, String measureName) {
	        DescriptionPath a = getPath(chain, residueNumber - 1, "C");
	        DescriptionPath b = getPath(chain, residueNumber, "N");
	        DescriptionPath c = getPath(chain, residueNumber, "CA");
	        DescriptionPath d = getPath(chain, residueNumber, "C");
	        
			return new AtomTorsionMeasure(measureName, a, b, c, d);
		}
		
		public AtomTorsionMeasure createPsi(ChainDescription chain, int residueNumber, String measureName) {
			DescriptionPath a = getPath(chain, residueNumber, "N");
			DescriptionPath b = getPath(chain, residueNumber, "CA");
			DescriptionPath c = getPath(chain, residueNumber, "C");
			DescriptionPath d = getPath(chain, residueNumber + 1, "N");
		    
			return new AtomTorsionMeasure(measureName, a, b, c, d);
		}
		
	}
	
	public MeasureBuilder measures() {
		return new MeasureBuilder(chain);
	}
	
	public static class AtomListDescriptionBuilder {
		
		private ChainDescription chain;
		
		public AtomListDescriptionBuilder(ChainDescription chain) {
			this.chain = chain;
		}
		
		private ChainDescription getChainDescription(String chainName) {
			return this.chain;	// TODO
		}

		public AtomTorsionRangeDescription createPhiRangeDescription(
				String chainName, double midPoint, double range, int residueNumber, String listDescriptionName) {
			return createPhi(getChainDescription(chainName), residueNumber, midPoint, range, listDescriptionName);
		}

		public AtomTorsionRangeDescription createPsiRangeDescription(
				String chainName, double midPoint, double range, int residueNumber, String listDescriptionName) {
			return createPsi(getChainDescription(chainName), residueNumber, midPoint, range, listDescriptionName);
		}

		public HBondDescription createHBondDescription(
				String chainName, double haMax, double dhaMin, double haaMin, 
				int donorNumber, int acceptorNumber, String listDescriptionName) {
			ChainDescription currentChain = getChainDescription(chainName);

			DescriptionPath a = getPath(currentChain, donorNumber, "N");
			DescriptionPath b = getPath(currentChain, donorNumber, "H");
			DescriptionPath c = getPath(currentChain, acceptorNumber, "O");
			DescriptionPath d = getPath(currentChain, acceptorNumber, "C");	// TODO

			return new HBondDescription("HBond", haMax, dhaMin, haaMin, a, b, c, d);
		}
		
		private AtomTorsionRangeDescription createPhi(
				ChainDescription chain, int residueNumber, double midPoint, double range, String measureName) {
	        DescriptionPath a = getPath(chain, residueNumber - 1, "C");
	        DescriptionPath b = getPath(chain, residueNumber, "N");
	        DescriptionPath c = getPath(chain, residueNumber, "CA");
	        DescriptionPath d = getPath(chain, residueNumber, "C");
	        
			return new AtomTorsionRangeDescription(measureName, midPoint - range, midPoint + range, a, b, c, d);
		}
		
		public AtomTorsionRangeDescription createPsi(
				ChainDescription chain, int residueNumber, double midPoint, double range, String measureName) {
			DescriptionPath a = getPath(chain, residueNumber, "N");
			DescriptionPath b = getPath(chain, residueNumber, "CA");
			DescriptionPath c = getPath(chain, residueNumber, "C");
			DescriptionPath d = getPath(chain, residueNumber + 1, "N");
		    
			return new AtomTorsionRangeDescription(measureName, midPoint - range, midPoint + range, a, b, c, d);
		}
		
	}
	
	public AtomListDescriptionBuilder listDescriptions() {
		return new AtomListDescriptionBuilder(chain);
	}

}
