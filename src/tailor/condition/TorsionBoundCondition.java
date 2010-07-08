package tailor.condition;

import tailor.description.Description;
import tailor.engine.Match;
import tailor.geometry.CenterFinder;
import tailor.geometry.Geometry;
import tailor.geometry.Vector;


public class TorsionBoundCondition implements Condition {
	
	private String name;
	private String letterSymbol;
	private Description descriptionA;
	private Description descriptionB;
	private Description descriptionC;
	private Description descriptionD;
	private double midPoint;
	private double range;
	
	/**
	 * A partial Condition, with just the numeric values filled. Use with care.
	 * 
	 * @param name
	 * @param midPoint
	 * @param range
	 */
	public TorsionBoundCondition(String name, double midPoint, double range) {
		this.name = name;
		this.midPoint = midPoint;
		this.range = range;
		this.letterSymbol = "";
	}
	
	/**
	 * A Condition that sets a bound on what a torsion between 4 Descriptions (probably atoms) can be.
	 * 
	 * @param name
	 * @param descriptionA
	 * @param descriptionB
	 * @param descriptionC
	 * @param descriptionD
	 * @param midPoint
	 * @param range
	 */
	public TorsionBoundCondition(String name, Description descriptionA, Description descriptionB, 
			Description descriptionC, Description descriptionD, double midPoint, double range) {
		this(name, midPoint, range);
		this.descriptionA = descriptionA;
		this.descriptionB = descriptionB;
		this.descriptionC = descriptionC;
		this.descriptionD = descriptionD;
	}
	

	public Description getDescriptionA() {
		return this.descriptionA;
	}
	
	public Description getDescriptionB() {
		return this.descriptionB;
	}
	
	public Description getDescriptionC() {
		return this.descriptionC;
	}
	
	public Description getDescriptionD() {
		return this.descriptionD;
	}
	
	public void setDescriptionA(Description descriptionA) {
		this.descriptionA = descriptionA;
	}
	
	public void setDescriptionB(Description descriptionB) {
		this.descriptionB = descriptionB;
	}
	
	public void setDescriptionC(Description descriptionC) {
		this.descriptionC = descriptionC;
	}
	
	public void setDescriptionD(Description descriptionD) {
		this.descriptionD = descriptionD;
	}
	
	public String getName() {
		return this.name;
	}
	
	public double getMidPoint() {
		return this.midPoint;
	}
	
	public double getRange() {
		return this.range;
	}
	
	public boolean contains(Description d) {
		return this.descriptionD.contains(d) 
				|| this.descriptionC.contains(d)
				|| this.descriptionB.contains(d)
				|| this.descriptionA.contains(d);
	}

    public Object clone() {
    	TorsionBoundCondition clonedCopy 
    		= new TorsionBoundCondition(this.name, this.midPoint, this.range);
    	clonedCopy.letterSymbol = this.letterSymbol;
    	clonedCopy.descriptionA = (Description) this.descriptionA.clone();
    	clonedCopy.descriptionB = (Description) this.descriptionB.clone();
    	clonedCopy.descriptionC = (Description) this.descriptionC.clone();
    	clonedCopy.descriptionD = (Description) this.descriptionD.clone();
    	return clonedCopy;
    }
    
	public boolean equals(Condition other) {
		if (other instanceof TorsionBoundCondition) {
			TorsionBoundCondition o = (TorsionBoundCondition) other;
			if (this.name.equals(o.name) &&
					this.descriptionA == o.descriptionA &&
					this.descriptionB == o.descriptionB &&
					this.descriptionC == o.descriptionC &&
					this.descriptionD == o.descriptionD &&
					this.midPoint == o.midPoint &&
					this.range == o.range) {
				return true;
			}
		}
		return false;
	}

	public boolean satisfiedBy(Match match) {
	    Vector a = CenterFinder.findCenter(descriptionA, match);
	    Vector b = CenterFinder.findCenter(descriptionB, match);
	    Vector c = CenterFinder.findCenter(descriptionC, match);
	    Vector d = CenterFinder.findCenter(descriptionD, match);

	    double result = Geometry.torsion(a, b, c, d);
	    boolean satisfied = 
	        midPoint - range < result && midPoint + range > result;
	    if (satisfied) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
	public String getLetterSymbol() {
		return this.letterSymbol;
	}
	
	public void setLetterSymbol(String letterSymbol) {
		this.letterSymbol = letterSymbol;
	}
	
	public String makeTorsionLabel() {
		double lower = this.midPoint - this.range;
		double upper = this.midPoint + this.range;
		return String.format("%.0f < %s < %.0f", lower, this.letterSymbol, upper);
	}
	
    public String toXml() {
    	String s = String.format("<TorsionBoundCondition midPoint=\"%s\" range=\"%s\">", 
    			this.midPoint, this.range);
    	s += "\t\t\t" + this.descriptionA.toXmlPathString();
    	s += "\t\t\t" + this.descriptionB.toXmlPathString();
    	s += "\t\t\t" + this.descriptionC.toXmlPathString();
    	s += "\t\t\t" + this.descriptionD.toXmlPathString();
    	s += "</TorsionBoundCondition>";
    	return s;
    }
	
	public String toString() {
		return String.format("%s (%s : %s)", this.name, this.midPoint - this.range, this.midPoint + this.range);
	}

}
