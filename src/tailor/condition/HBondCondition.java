package tailor.condition;

import tailor.description.Description;
import tailor.match.Match;
import tailor.measurement.HBondMeasure;
import tailor.measurement.HBondMeasurement;


/**
 * @author maclean
 *
 */
public class HBondCondition implements Condition {
    
    private String name;
    
    private HBondMeasure hBondMeasure;
    
    private double haMax;
    
    private double dhaMin;
    
    private double haaMin;
    
    private boolean isNegated;
    
    /**
     * An anonymous HBond that will be called "HBond".
     * 
     * @param donorAtomDescription
     * @param hydrogenAtomDescription
     * @param acceptorAtomDescription
     * @param attachedAtomDescription
     */
    public HBondCondition(Description donorAtomDescription, 
            			  Description hydrogenAtomDescription, 
            			  Description acceptorAtomDescription, 
            			  Description attachedAtomDescription, 
            			  double haMax, double dhaMin, double haaMin) {
        this("HBond", donorAtomDescription, 
                	  hydrogenAtomDescription, 
                	  acceptorAtomDescription, 
                	  attachedAtomDescription, haMax, dhaMin, haaMin);
    }

    /**
     * @param name
     * @param donorAtomDescription
     * @param hydrogenAtomDescription
     * @param acceptorAtomDescription
     * @param attachedAtomDescription
     */
    public HBondCondition(String name, Description donorAtomDescription, 
            Description hydrogenAtomDescription, Description acceptorAtomDescription, 
            Description attachedAtomDescription, double haMax, double dhaMin, double haaMin) {
        this.name = name;
        this.hBondMeasure = 
                new HBondMeasure(
                    name, 
                    donorAtomDescription, 
                    hydrogenAtomDescription, 
                    acceptorAtomDescription, 
                    attachedAtomDescription);
        this.haMax = haMax;
        this.dhaMin = dhaMin;
        this.haaMin = haaMin;
    }
    
    public void setNegated(boolean isNegated) {
        this.isNegated = isNegated;
    }
    
    public Description getDonorAtomDescription() {
		return this.hBondMeasure.getDonorAtomDescription();
	}

	public Description getHydrogenAtomDescription() {
		return this.hBondMeasure.getHydrogenAtomDescription();
	}

	public Description getAcceptorAtomDescription() {
		return this.hBondMeasure.getAcceptorAtomDescription();
	}

	public Description getAttachedAtomDescription() {
		return this.hBondMeasure.getAttachedAtomDescription();
	}
	
	public boolean contains(Description d) {
		return getAcceptorAtomDescription().contains(d)
				|| getAttachedAtomDescription().contains(d)
				|| getDonorAtomDescription().contains(d)
				|| getHydrogenAtomDescription().contains(d);
	}

	public boolean equals(Condition other) {
        return false;
    }

    public boolean satisfiedBy(Match match) {
        HBondMeasurement hBondMeasurement = hBondMeasure.measure(match);
        
        boolean satisfied = false;
        double h_a = hBondMeasurement.getHaDistance();
//        System.err.println("h_a " + h_a);
        
        if (h_a < this.haMax) {
            double dha = hBondMeasurement.getDhaAngle();
//            System.err.println("dha " + dha);
            if (dha > this.dhaMin) {
                double haa = hBondMeasurement.getHaaAngle();
//                System.err.println("haa " + haa);
                if (haa > this.haaMin) {
                    satisfied = true;
                }
            }
        }
        
        if (this.isNegated) {
            return !satisfied;
        } else {
            return satisfied;
        }
    }
    
    public String toXml() {
    	String s = String.format(
    			"\t\t<HBondCondition haMax=\"%s\" dhaMin=\"%s\" haaMin=\"%s\">\n", 
    			this.haMax, this.dhaMin, this.haaMin);
    	s += "\t\t\t" + getDonorAtomDescription().toXmlPathString() + "\n";
    	s += "\t\t\t" + getHydrogenAtomDescription().toXmlPathString() + "\n";
    	s += "\t\t\t" + getAcceptorAtomDescription().toXmlPathString() + "\n";
    	s += "\t\t\t" + getAttachedAtomDescription().toXmlPathString() + "\n";
    	s += "\t\t</HBondCondition>\n";
    	return s;
    }
    
    public String toString() {
        return this.name + " (" + getDonorAtomDescription().toPathString() 
        				 + " -> " + getAcceptorAtomDescription().toPathString() + ")";
    }

}
