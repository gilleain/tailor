package tailor.msdmotif;

import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;
import tailor.condition.TorsionBoundCondition;

/**
 * A very crude and limited way to transform tailor.core.Description hierarchies
 * into xml files suitable to send to msdmotif as a query. Hopefully
 * the query language for msdmotif will be radically improved soon...
 * 
 * @author maclean
 *
 */
public class DescriptionToXmlQueryTranslator {
	
	public static String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE query SYSTEM \"http://www.ebi.ac.uk/msd-srv/msdmotif/query.dtd\">\n"; 

	public static String translate(Description description) {
        
        // TODO : type safety!
        ProteinDescription protein = (ProteinDescription) description;
        
		StringBuffer xmlString = new StringBuffer();
		
		// initial value which we expect to change
		double phiPsiDeviation = 0;
		
		xmlString.append(DescriptionToXmlQueryTranslator.makeDeclaration());
        
		for (ChainDescription chain : protein.getChainDescriptions()) {
			for (GroupDescription residue : chain.getGroupDescriptions()) {
                
				for (TorsionBoundCondition torsionBoundCondition : residue.getTorsionBoundConditions()) {
					String name = torsionBoundCondition.getName();
					double midPoint = torsionBoundCondition.getMidPoint();
					double range = torsionBoundCondition.getRange();
					if (range > phiPsiDeviation) {
						phiPsiDeviation = range;
					}
					if (name.equals("phi")) {
						xmlString.append(DescriptionToXmlQueryTranslator.makeTorsion(2, "PHI", midPoint));
					} else if (name.equals("psi")) {
						xmlString.append(DescriptionToXmlQueryTranslator.makeTorsion(2, "PSI", midPoint));
					}
				}
			}
		}
		
		// only now can we insert the header and open the query tag - had to wait for the phipsiDeviation
		boolean useUndefinedBonds = false;
		float eValue = 0.0f;
		String normalize = "seq50";
		xmlString.insert(0, DescriptionToXmlQueryTranslator.openQueryTag(useUndefinedBonds, phiPsiDeviation, eValue, normalize));
		xmlString.insert(0, DescriptionToXmlQueryTranslator.header);
		
		xmlString.append(DescriptionToXmlQueryTranslator.makeInteraction(1, "O"));
		xmlString.append(DescriptionToXmlQueryTranslator.makeInteraction(3, "O"));
		
		xmlString.append("</query>");
		return xmlString.toString();
	}
	
	public static String makeInteraction(int proteinResidueNumber, String proteinAtomName) {
		return String.format("<interaction>p.%s.%s&lt;-&gt;r</interaction>\n", proteinResidueNumber, proteinAtomName);
	}
	
	public static String makeTorsion(int residueNumber, String torsionName, double torsionMidPoint) {
		return String.format("<interaction>p.%s.%s = %s</interaction>\n", residueNumber, torsionName, torsionMidPoint);
	}
	
	public static String makeDeclaration() {
		return "<declaration>\n<ligand>x r</ligand>\n<pattern>xxx p</pattern>\n</declaration>\n";
	}
	
	public static String openQueryTag(boolean useUndefinedBonds, double phiPsiDeviation, float eValue, String normalize) {
		String useUndefinedBondsString = "no";
		if (useUndefinedBonds) {
			useUndefinedBondsString = "yes";
		}
		String eValueString = "";
		if (eValue == 0.0f) {
			eValueString = "auto";
		} else {
			eValueString = String.valueOf(eValue);
		}
		return String.format("<query useUndefinedBonds=\"%s\" phipsiDeviation=\"%1.0f\" eValue=\"%s\" normalize=\"%s\">\n", useUndefinedBondsString, phiPsiDeviation, eValueString, normalize);
	}
	
	public static void main(String[] args) {
		ProteinDescription p = new ProteinDescription("Test");
		ChainDescription chain = new ChainDescription();
		GroupDescription residue = new GroupDescription();
//        AtomDescription c = new AtomDescription("C");
		AtomDescription n = new AtomDescription("N");
//        AtomDescription ca = new AtomDescription("CA");
		
        p.addChainDescription(chain);
        chain.addGroupDescription(residue);
		residue.addAtomDescription(n);
        
		residue.addAtomCondition(new TorsionBoundCondition("phi", p, p, p, p, -50.0, 10.0));
		residue.addAtomCondition(new TorsionBoundCondition("psi", p, p, p, p, 100.0, 20.0));
		
		
		System.out.println(DescriptionToXmlQueryTranslator.translate(p));
	}
}
