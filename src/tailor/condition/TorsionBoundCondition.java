package tailor.condition;

import tailor.description.Description;
import tailor.engine.Match;
import tailor.measure.TorsionMeasure;


public class TorsionBoundCondition implements Condition {
	
	private String name;
	private String letterSymbol;
	private TorsionMeasure torsionMeasure;
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
	 * A Condition that sets a bound on what a torsion between 4 Descriptions 
	 * (probably atoms) can be.
	 * 
	 * @param name
	 * @param descriptionA
	 * @param descriptionB
	 * @param descriptionC
	 * @param descriptionD
	 * @param midPoint
	 * @param range
	 */
	public TorsionBoundCondition(String name, Description descriptionA, 
	        Description descriptionB, Description descriptionC, 
	        Description descriptionD, double midPoint, double range) {
		this(name, midPoint, range);
		this.torsionMeasure = 
		    new TorsionMeasure(
		            descriptionA, descriptionB, descriptionC, descriptionD);
	}
	

	public Description getDescriptionA() {
		return this.torsionMeasure.getDescriptionA();
	}
	
	public Description getDescriptionB() {
	    return this.torsionMeasure.getDescriptionB();
	}
	
	public Description getDescriptionC() {
	    return this.torsionMeasure.getDescriptionC();
	}
	
	public Description getDescriptionD() {
	    return this.torsionMeasure.getDescriptionD();
	}
	
	public void setDescriptionA(Description descriptionA) {
	    this.torsionMeasure.setDescriptionA(descriptionA);
	}
	
	public void setDescriptionB(Description descriptionB) {
	    this.torsionMeasure.setDescriptionA(descriptionB);
	}
	
	public void setDescriptionC(Description descriptionC) {
	    this.torsionMeasure.setDescriptionA(descriptionC);
	}
	
	public void setDescriptionD(Description descriptionD) {
	    this.torsionMeasure.setDescriptionA(descriptionD);
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
//		return this.descriptionD.contains(d) 
//				|| this.descriptionC.contains(d)
//				|| this.descriptionB.contains(d)
//				|| this.descriptionA.contains(d);
	    // TODO
	    return false;
	}

    public Object clone() {
    	TorsionBoundCondition clonedCopy 
    		= new TorsionBoundCondition(this.name, this.midPoint, this.range);
    	clonedCopy.letterSymbol = this.letterSymbol;
//    	clonedCopy.torsionMeasure = this.torsionMeasure.clone(); TODO
    	return clonedCopy;
    }
    
	public boolean equals(Condition other) {
		if (other instanceof TorsionBoundCondition) {
			TorsionBoundCondition o = (TorsionBoundCondition) other;
			if (this.name.equals(o.name) &&
					this.torsionMeasure == o.torsionMeasure &&
					this.midPoint == o.midPoint &&
					this.range == o.range) {
				return true;
			}
		}
		return false;
	}

	public boolean satisfiedBy(Match match) {
	    double result = torsionMeasure.calculate(match);
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
    	String s = String.format(
    	        "<TorsionBoundCondition midPoint=\"%s\" range=\"%s\">", 
    			this.midPoint, this.range);
    	s += "\t\t\t" + this.torsionMeasure.getDescriptionA().toXmlPathString();
    	s += "\t\t\t" + this.torsionMeasure.getDescriptionB().toXmlPathString();
    	s += "\t\t\t" + this.torsionMeasure.getDescriptionC().toXmlPathString();
    	s += "\t\t\t" + this.torsionMeasure.getDescriptionD().toXmlPathString();
    	s += "</TorsionBoundCondition>";
    	return s;
    }
	
	public String toString() {
		return String.format("%s (%s : %s)", 
		     this.name, this.midPoint - this.range, this.midPoint + this.range);
	}

}
