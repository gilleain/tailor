package tailor.description;

import tailor.description.atom.AtomTorsionRangeDescription;
import tailor.description.atom.HBondDescription;
import tailor.measure.AtomTorsionMeasure;
import tailor.measure.HBondMeasure;
import tailor.structure.Level;

public class DescriptionFactory {

	public HBondMeasure createHBondMeasure(int startResidueNumber, int endResidueNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addChainToProtein(String string) {
		// TODO Auto-generated method stub
		
	}

	public void addMultipleResiduesToChain(String string, int numberOfResidues) {
		// TODO Auto-generated method stub
		
	}

	public ChainDescription getChainDescription(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public ProteinDescription getProduct() {
		// TODO Auto-generated method stub
		return null;
	}

	public AtomTorsionRangeDescription createPhiCondition(double midPoint, double range, int residueNumber,
			String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public AtomTorsionRangeDescription createPsiCondition(double midPoint, double range, int residueNumber,
			String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public AtomTorsionMeasure createPhiMeasure(String torsionName, int residueNumber, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public AtomTorsionMeasure createPsiMeasure(String torsionName, int residueNumber, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canHydrogenBond(AtomDescription a, AtomDescription b) {
		// TODO Auto-generated method stub
		return false;
	}

	public void addTorsionMeasureToChain(AtomTorsionMeasure torsionMeasure, String string) {
		// TODO Auto-generated method stub
		
	}

	public static Description createFromLevel(Level protein, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public HBondDescription createHBondCondition(double haMax, double dhaMin, double haaMin, int donorNumber,
			int acceptorNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAddBackboneAmineHydrogens(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void addResidueToChain(String string) {
		// TODO Auto-generated method stub
		
	}

	public void addResidues(int i) {
		// TODO Auto-generated method stub
		
	}

	public void addHBondConditionToChain(HBondDescription hBondCondition, String string) {
		// TODO Auto-generated method stub
		
	}

	public AtomTorsionMeasure createPhiMeasure(String string, int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public AtomTorsionMeasure createPsiMeasure(String string, int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addPsiConditionToChain(AtomTorsionRangeDescription torsionCondition, String string) {
		// TODO Auto-generated method stub
		
	}

	public static Level getSubLevel(Level currentLevel) {
		// TODO Auto-generated method stub
		return null;
	}

}
