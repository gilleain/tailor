package tailor.measure;

import tailor.datasource.Structure;
import tailor.description.Description;
import tailor.description.DescriptionException;
import tailor.geometry.Geometry;
import tailor.geometry.Vector;


public class AngleMeasure extends Measure {
    
    private Description descriptionA;
    
    private Description descriptionB;
    
    private Description descriptionC;

    public AngleMeasure(String name, Description descriptionA, Description descriptionB, Description descriptionC) {
        super(name);
        this.descriptionA = descriptionA;
        this.descriptionB = descriptionB;
        this.descriptionC = descriptionC;
    }
    
    /**
     * @param descriptionA
     * @param descriptionB
     * @param descriptionC
     */
    public AngleMeasure(Description descriptionA, Description descriptionB, Description descriptionC) {
        this("Angle", descriptionA, descriptionB, descriptionC);
    }


    @Override
    public Measurement measure(Structure structure) throws DescriptionException {
        Vector a = this.descriptionA.findStructureCenter(structure);
        Vector b = this.descriptionB.findStructureCenter(structure);
        Vector c = this.descriptionC.findStructureCenter(structure);
        
        double angle = Geometry.angle(a, b, c);
        return new Measurement(this.getName(), angle);
    }
    
    public String toString() {
        return "a (" + this.descriptionA.toPathString() + ", " 
                    + this.descriptionB.toPathString() + ", "
                    + this.descriptionC.toPathString() + ") ";
    }

}
