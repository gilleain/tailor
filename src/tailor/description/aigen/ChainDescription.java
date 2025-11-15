package tailor.description.aigen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import tailor.datasource.aigen.Feature;
import tailor.structure.ChainType;

class ChainDescription extends Description {
	private String levelCode;
	private ChainType chainType;

	public ChainDescription(Map<String, Object> propertyConditions) {
		super(propertyConditions);
		this.levelCode = "C";
	}

	public ChainDescription copy() {
		ChainDescription copy = new ChainDescription(new HashMap<>(this.properties));
		copy.children = new ArrayList<>(this.children);
		copy.conditions = new ArrayList<>(this.conditions);
		return copy;
	}
	
	public ChainType getChainType() {
		return chainType;
	}

	public void createResidues(int n) {
		createResidueFromToInclusive(1, n);
	}

	public void createResidueFromToInclusive(int i, int j) {
		for (int x = i - 1; x < j; x++) {
			createResidueWithBackbone(x);
		}
	}

	public void createResidueWithBackbone(int i) {
		Map<String, Object> props = new HashMap<>();
		props.put("position", i + 1);
		ResidueDescription residue = new ResidueDescription(props);

		for (String atomName : new String[] { "N", "CA", "C", "O", "H" }) {
			residue.addAtom(atomName);
		}
		addResidue(residue);
	}

	public void addResidue(ResidueDescription residue) {
		children.add(residue);
	}

	public ResidueDescription getResidue(int i) {
		return (ResidueDescription) children.get(i - 1);
	}
	
	public List<ResidueDescription> getResidueDescriptions() {
		return this.children.stream().map(r -> (ResidueDescription)r).collect(Collectors.toList());
	}

	public ResidueDescription selectResidue(int i) {
		return ((ResidueDescription) children.get(i - 1)).copy();
	}

	public String getName() {
		return (String) getProperty("chainID");
	}

	public List<Object> createBackboneHBondConditions(List<int[]> donorAcceptorResidueNumbers, double maxHO,
			double minHOC, double minNHO) {
		List<Object> conditions = new ArrayList<>();
		for (int[] pair : donorAcceptorResidueNumbers) {
			conditions.add(createBackboneHBondCondition(pair[0], pair[1], maxHO, minHOC, minNHO));
		}
		return conditions;
	}

	public Object createBackboneHBondCondition(int donorResidueNumber, int acceptorResidueNumber, double maxHO,
			double minHOC, double minNHO) {
		Description N = SelectionHelper.constructSelection(null, "Protein", donorResidueNumber, "N");
		Description H = SelectionHelper.constructSelection(null, "Protein", donorResidueNumber, "H");
		Description C = SelectionHelper.constructSelection(null, "Protein", acceptorResidueNumber, "C");
		Description O = SelectionHelper.constructSelection(null, "Protein", acceptorResidueNumber, "O");

		String name = String.format("NH(%d)->CO(%d)", donorResidueNumber, acceptorResidueNumber);

		// Object bond = new HBondCondition(N, H, O, C, maxHO, minNHO, minHOC, name);
		// addCondition(bond);
		// return bond;
		return null; // Placeholder
	}

	public List<Object> createBackboneHBondMeasures(List<int[]> donorAcceptorResidueNumbers) {
		List<Object> measures = new ArrayList<>();
		for (int[] pair : donorAcceptorResidueNumbers) {
			measures.add(createBackboneHBondMeasure(pair[0], pair[1]));
		}
		return measures;
	}

	public Object createBackboneHBondMeasure(int donorResidueNumber, int acceptorResidueNumber) {
		Description N = SelectionHelper.constructSelection(null, "Protein", donorResidueNumber, "N");
		Description H = SelectionHelper.constructSelection(null, "Protein", donorResidueNumber, "H");
		Description C = SelectionHelper.constructSelection(null, "Protein", acceptorResidueNumber, "C");
		Description O = SelectionHelper.constructSelection(null, "Protein", acceptorResidueNumber, "O");

		// Object bond = new HBondMeasure(N, H, O, C);
		// bond.setName(String.format("NH(%d)->CO(%d)", donorResidueNumber,
		// acceptorResidueNumber));
		// return bond;
		return null; // Placeholder
	}

	public List<Object> createPhiPsiMeasures(List<Integer> residueNumbers) {
		List<Object> measures = new ArrayList<>();
		for (Integer residueNumber : residueNumbers) {
			Object[] pair = createPhiPsiMeasure(residueNumber);
			measures.add(pair[0]);
			measures.add(pair[1]);
		}
		return measures;
	}

	public Object[] createPhiPsiMeasure(int residueNumber) {
		return new Object[] { createPhiMeasure(residueNumber), createPsiMeasure(residueNumber) };
	}

	public Object createPhiMeasure(int residueNumber) {
		int i = residueNumber;
		Object phi = createTorsionMeasure(i - 1, "C", i, "N", i, "CA", i, "C");
		// phi.setName("phi" + residueNumber);
		return phi;
	}

	public Object createPsiMeasure(int residueNumber) {
		int i = residueNumber;
		Object psi = createTorsionMeasure(i, "N", i, "CA", i, "C", i + 1, "N");
		// psi.setName("psi" + residueNumber);
		return psi;
	}

	public Object createPhiBoundCondition(int residueNumber, double center, double range) {
		int i = residueNumber;
		Object condition = createTorsionBoundCondition(i - 1, "C", i, "N", i, "CA", i, "C", center, range, "phi" + i);
		addCondition(condition);
		return condition;
	}

	public Object createPsiBoundCondition(int residueNumber, double center, double range) {
		int i = residueNumber;
		Object condition = createTorsionBoundCondition(i, "N", i, "CA", i, "C", i + 1, "N", center, range, "psi" + i);
		addCondition(condition);
		return condition;
	}

	public Object createTorsionBoundCondition(int firstR, String firstA, int secondR, String secondA, int thirdR,
			String thirdA, int fourthR, String fourthA, double center, double range, String name) {
		Description a = SelectionHelper.constructSelection(null, "Protein", firstR, firstA);
		Description b = SelectionHelper.constructSelection(null, "Protein", secondR, secondA);
		Description c = SelectionHelper.constructSelection(null, "Protein", thirdR, thirdA);
		Description d = SelectionHelper.constructSelection(null, "Protein", fourthR, fourthA);

		// if (name == null) {
		// return new TorsionBoundCondition(a, b, c, d, center, range);
		// } else {
		// return new TorsionBoundCondition(a, b, c, d, center, range, name);
		// }
		return null; // Placeholder
	}

	public Object createTorsionMeasure(int firstR, String firstA, int secondR, String secondA, int thirdR,
			String thirdA, int fourthR, String fourthA) {
		Description a = SelectionHelper.constructSelection(null, "Protein", firstR, firstA);
		Description b = SelectionHelper.constructSelection(null, "Protein", secondR, secondA);
		Description c = SelectionHelper.constructSelection(null, "Protein", thirdR, thirdA);
		Description d = SelectionHelper.constructSelection(null, "Protein", fourthR, fourthA);

		// return new TorsionMeasure(a, b, c, d);
		return null; // Placeholder
	}

	@Override
	public String toString() {
		Object chainID = getProperty("chainID");
		if (chainID != null)
			return chainID.toString();

		Object chainType = getProperty("chainType");
		if (chainType != null)
			return chainType.toString();

		return "?";
	}

	@Override
	public String getLevelCode() {
		return levelCode;
	}

	@Override
	public int length() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean describes(Feature feature) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Class<? extends Feature> getFeatureType() {
		// TODO Auto-generated method stub
		return null;
	}
}