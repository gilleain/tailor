package tailor.measure;

import tailor.datasource.Structure;
import tailor.description.Description;
import tailor.description.DescriptionException;
import tailor.geometry.Geometry;
import tailor.geometry.Vector;


/**
 * @author maclean
 *
 */
public class TorsionMeasure extends Measure {
    
    private Description descriptionA;
    
    private Description descriptionB;
    
    private Description descriptionC;
    
    private Description descriptionD;
    
    public TorsionMeasure(String name) {
    	super(name);
    }

    public TorsionMeasure(String name, Description descriptionA, 
            Description descriptionB, Description descriptionC, Description descriptionD) {
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

    @Override
    public Measurement measure(Structure structure) throws DescriptionException {
        Vector a = this.descriptionA.findStructureCenter(structure);
        Vector b = this.descriptionB.findStructureCenter(structure);
        Vector c = this.descriptionC.findStructureCenter(structure);
        Vector d = this.descriptionD.findStructureCenter(structure);
        
        double torsion = Geometry.torsion(a, b, c, d);
        return new Measurement(this.getName(), torsion);
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
