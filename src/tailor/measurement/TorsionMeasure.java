package tailor.measurement;

import tailor.description.Description;
import tailor.geometry.Geometry;
import tailor.geometry.Vector;
import tailor.match.Match;


/**
 * Measures the torsion (or dihedral angle) between four points.
 * 
 * @author maclean
 *
 */
public class TorsionMeasure extends GeometricMeasure implements Measure<DoubleMeasurement> {
    
    private Description descriptionA;
    
    private Description descriptionB;
    
    private Description descriptionC;
    
    private Description descriptionD;
    
    // XXX why?
    public TorsionMeasure(String name) {
        super(name);
    }

    public TorsionMeasure(String name, Description descriptionA, 
            Description descriptionB, Description descriptionC, 
            Description descriptionD) {
        super(name);
        this.descriptionA = descriptionA;
        this.descriptionB = descriptionB;
        this.descriptionC = descriptionC;
        this.descriptionD = descriptionD;
    }
    
    public TorsionMeasure(Description descriptionA, Description descriptionB,
                            Description descriptionC, Description descriptionD) {
        this("Torsion", descriptionA, descriptionB, descriptionC, descriptionD);
    }
    
    public double calculate(Match match) {
        Vector a = getPoint(descriptionA, match);
        Vector b = getPoint(descriptionB, match);
        Vector c = getPoint(descriptionC, match);
        Vector d = getPoint(descriptionD, match);
        return Geometry.torsion(a, b, c, d);        
    }

    @Override
    public DoubleMeasurement measure(Match match) {
        return new DoubleMeasurement(calculate(match));
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
    
    public String toString() {
        return "t (" + this.descriptionA.toPathString() + ", " 
                    + this.descriptionB.toPathString() + ", "
                    + this.descriptionC.toPathString() + ", "
                    + this.descriptionD.toPathString() + ") ";
    }

}
