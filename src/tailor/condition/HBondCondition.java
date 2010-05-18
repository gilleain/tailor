package tailor.condition;

import tailor.datasource.Structure;
import tailor.description.Description;
import tailor.geometry.Geometry;
import tailor.geometry.Vector;


public class HBondCondition implements Condition {
    
    private String name;
    
    private Description donorAtomDescription;
    
    private Description hydrogenAtomDescription;
    
    private Description acceptorAtomDescription;
    
    private Description attachedAtomDescription;
    
    private double haMax;
    
    private double dhaMin;
    
    private double haaMin;
    
    private boolean isNegated;
    
    /**
     * A partial Condition, with only the values filled. Use with care.
     * 
     * @param haMax
     * @param dhaMin
     * @param haaMin
     */
    public HBondCondition(double haMax, double dhaMin, double haaMin) {
    	this.name = "HBond";
    	this.haMax = haMax;
        this.dhaMin = dhaMin;
        this.haaMin = haaMin;
    }
    
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
        this.donorAtomDescription = donorAtomDescription;
        this.hydrogenAtomDescription = hydrogenAtomDescription;
        this.acceptorAtomDescription = acceptorAtomDescription;
        this.attachedAtomDescription = attachedAtomDescription;
        this.haMax = haMax;
        this.dhaMin = dhaMin;
        this.haaMin = haaMin;
    }
    
    public void setNegated(boolean isNegated) {
        this.isNegated = isNegated;
    }
    
    public Description getDonorAtomDescription() {
		return this.donorAtomDescription;
	}

	public Description getHydrogenAtomDescription() {
		return this.hydrogenAtomDescription;
	}

	public Description getAcceptorAtomDescription() {
		return this.acceptorAtomDescription;
	}

	public Description getAttachedAtomDescription() {
		return this.attachedAtomDescription;
	}

    public void setDonorAtomDescription(Description donorAtomDescription) {
		this.donorAtomDescription = donorAtomDescription;
	}

	public void setHydrogenAtomDescription(Description hydrogenAtomDescription) {
		this.hydrogenAtomDescription = hydrogenAtomDescription;
	}

	public void setAcceptorAtomDescription(Description acceptorAtomDescription) {
		this.acceptorAtomDescription = acceptorAtomDescription;
	}

	public void setAttachedAtomDescription(Description attachedAtomDescription) {
		this.attachedAtomDescription = attachedAtomDescription;
	}
	
	public boolean contains(Description d) {
		return this.acceptorAtomDescription.contains(d)
				|| this.attachedAtomDescription.contains(d)
				|| this.donorAtomDescription.contains(d)
				|| this.hydrogenAtomDescription.contains(d);
	}

    public Object clone() {
    	HBondCondition clonedCopy 
			= new HBondCondition(this.haMax, this.dhaMin, this.haaMin);
		clonedCopy.name = this.name;
		clonedCopy.acceptorAtomDescription 
			= (Description) this.acceptorAtomDescription.clone();
		clonedCopy.attachedAtomDescription 
			= (Description) this.attachedAtomDescription.clone();
		clonedCopy.donorAtomDescription 
			= (Description) this.donorAtomDescription.clone();
		clonedCopy.hydrogenAtomDescription 
			= (Description) this.hydrogenAtomDescription.clone();
		return clonedCopy;
    }
	
	public boolean equals(Condition other) {
        return false;
    }

    public boolean satisfiedBy(Structure structure) {
        Vector d = this.donorAtomDescription.findStructureCenter(structure);
        Vector h = this.hydrogenAtomDescription.findStructureCenter(structure);
        Vector a = this.acceptorAtomDescription.findStructureCenter(structure);
        Vector aa = this.attachedAtomDescription.findStructureCenter(structure);
        
        boolean satisfied = false;
        double h_a = Geometry.distance(h, a);
//        System.err.println("h_a " + h_a);
        
        if (h_a < this.haMax) {
            double dha = Geometry.angle(d, h, a);
//            System.err.println("dha " + dha);
            if (dha > this.dhaMin) {
                double haa = Geometry.angle(h, a, aa);
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
    	s += "\t\t\t" + this.donorAtomDescription.toXmlPathString() + "\n";
    	s += "\t\t\t" + this.hydrogenAtomDescription.toXmlPathString() + "\n";
    	s += "\t\t\t" + this.acceptorAtomDescription.toXmlPathString() + "\n";
    	s += "\t\t\t" + this.attachedAtomDescription.toXmlPathString() + "\n";
    	s += "\t\t</HBondCondition>\n";
    	return s;
    }
    
    public String toString() {
        return this.name + " (" + this.donorAtomDescription.toPathString() 
        				 + " -> " + this.acceptorAtomDescription.toPathString() + ")";
    }

}
