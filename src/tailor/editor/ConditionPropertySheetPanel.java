package tailor.editor;

import java.awt.CardLayout;

import javax.swing.JPanel;

import tailor.api.Torsion;
import tailor.condition.HBondCondition;
import tailor.condition.TorsionBoundCondition;
import tailor.description.DescriptionFactory;

public class ConditionPropertySheetPanel extends JPanel {
	
	public static String TORSION_TAG = "TORSION_SHEET";
	public static String H_BOND_TAG = "HBOND_SHEET";
	
	private CardLayout cardLayout;
	private TorsionConditionPropertySheet torsionSheet;
	private HBondConditionPropertySheet hBondSheet;
	
	public ConditionPropertySheetPanel(DescriptionFactory descriptionFactory) {
	    
		this.cardLayout = new CardLayout(); 
		this.setLayout(this.cardLayout);
		
		this.torsionSheet = new TorsionConditionPropertySheet(descriptionFactory);
		this.add(this.torsionSheet, ConditionPropertySheetPanel.TORSION_TAG);
		
		this.hBondSheet = new HBondConditionPropertySheet(descriptionFactory);
		this.add(this.hBondSheet, ConditionPropertySheetPanel.H_BOND_TAG);
	}
	
	public void showSheet(String tag) {
		this.cardLayout.show(this, tag);
	}
	
	public void setHBondSheetResidues(int donorResidueNumber, int acceptorResidueNumber) {
		this.hBondSheet.setHBondResidues(donorResidueNumber, acceptorResidueNumber);
	}
	
	public void initialiseTorsionSheet(Torsion torsion) {
		this.torsionSheet.setToUnknownValues(torsion);
	}
	
	public void setTorsionSheetResidue(Torsion torsion, int start, int end) {
		this.torsionSheet.setValues(torsion, start, end);
	}
	
	public TorsionBoundCondition getTorsionCondition(int residueStart) {
		return this.torsionSheet.getCondition(residueStart);
	}
	
	public HBondCondition getHBondCondition(int residueStart, int residueEnd) {
		return this.hBondSheet.getCondition(residueStart, residueEnd);
	}

}
