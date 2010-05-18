package tailor.editor;

import java.awt.CardLayout;

import javax.swing.JPanel;

import tailor.measure.HBondMeasure;
import tailor.measure.TorsionMeasure;

public class MeasurePropertySheetPanel extends JPanel {
	
	public static String TORSION_TAG = "TORSION_SHEET";
	public static String H_BOND_TAG = "HBOND_SHEET";
	
	private CardLayout cardLayout;
	private TorsionMeasurePropertySheet torsionSheet;
	private HBondMeasurePropertySheet hBondSheet;
	
	public MeasurePropertySheetPanel() {
		this.cardLayout = new CardLayout(); 
		this.setLayout(this.cardLayout);
		
		this.torsionSheet = new TorsionMeasurePropertySheet();
		this.add(this.torsionSheet, MeasurePropertySheetPanel.TORSION_TAG);
		
		this.hBondSheet = new HBondMeasurePropertySheet();
		this.add(this.hBondSheet, MeasurePropertySheetPanel.H_BOND_TAG);
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
	
	public TorsionMeasure getTorsionMeasure() {
		return this.torsionSheet.getMeasure();
	}
	
	public HBondMeasure getHBondMeasure() {
		return this.hBondSheet.getMeasure();
	}

}
