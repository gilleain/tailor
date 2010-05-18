package tailor.editor;

import java.awt.CardLayout;

import javax.swing.JPanel;

import tailor.condition.HBondCondition;
import tailor.condition.TorsionBoundCondition;

public class ConditionPropertySheetPanel extends JPanel {
	
	public static String TORSION_TAG = "TORSION_SHEET";
	public static String H_BOND_TAG = "HBOND_SHEET";
	
	private CardLayout cardLayout;
	private TorsionConditionPropertySheet torsionSheet;
	private HBondConditionPropertySheet hBondSheet;
	
	public ConditionPropertySheetPanel() {
		this.cardLayout = new CardLayout(); 
		this.setLayout(this.cardLayout);
		
		this.torsionSheet = new TorsionConditionPropertySheet();
		this.add(this.torsionSheet, ConditionPropertySheetPanel.TORSION_TAG);
		
		this.hBondSheet = new HBondConditionPropertySheet();
		this.add(this.hBondSheet, ConditionPropertySheetPanel.H_BOND_TAG);
	}
	
	public void showSheet(String tag) {
		this.cardLayout.show(this, tag);
	}
	
	public void setHBondSheetResidues(int donorResidueNumber, int acceptorResidueNumber) {
		this.hBondSheet.setHBondResidues(donorResidueNumber, acceptorResidueNumber);
	}
	
	public void initialiseTorsionSheet(String name) {
		this.torsionSheet.setToUnknownValues(name);
	}
	
	public void setTorsionSheetResidue(String name, int start, int end) {
		this.torsionSheet.setValues(name, start, end);
	}
	
	public TorsionBoundCondition getTorsionCondition() {
		return this.torsionSheet.getCondition();
	}
	
	public HBondCondition getHBondCondition() {
		return this.hBondSheet.getCondition();
	}

}
