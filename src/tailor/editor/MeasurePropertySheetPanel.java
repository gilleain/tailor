package tailor.editor;

import java.awt.CardLayout;

import javax.swing.JPanel;

import tailor.description.DescriptionFactory;
import tailor.measurement.HBondMeasure;
import tailor.measurement.TorsionMeasure;

public class MeasurePropertySheetPanel extends JPanel {
	
	public static String TORSION_TAG = "TORSION_SHEET";
	public static String H_BOND_TAG = "HBOND_SHEET";
	
	private CardLayout cardLayout;
	private TorsionMeasurePropertySheet torsionSheet;
	private HBondMeasurePropertySheet hBondSheet;
	
	public MeasurePropertySheetPanel(DescriptionFactory descriptionFactory) {
		this.cardLayout = new CardLayout(); 
		this.setLayout(this.cardLayout);
		
		this.torsionSheet = new TorsionMeasurePropertySheet(descriptionFactory);
		this.add(this.torsionSheet, MeasurePropertySheetPanel.TORSION_TAG);
		
		this.hBondSheet = new HBondMeasurePropertySheet(descriptionFactory);
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
	
	public TorsionMeasure getTorsionMeasure(int residueNumber) {
		return this.torsionSheet.getMeasure(residueNumber);
	}
	
	public HBondMeasure getHBondMeasure(int donorNumber, int acceptorNumber) {
		return this.hBondSheet.getMeasure(donorNumber, acceptorNumber);
	}

}
