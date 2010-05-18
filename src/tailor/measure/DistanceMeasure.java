package tailor.measure;

import tailor.datasource.Structure;
import tailor.description.DescriptionException;
import tailor.description.Description;
import tailor.geometry.Geometry;
import tailor.geometry.Vector;

public class DistanceMeasure extends Measure {

	private Description descriptionA;
    
	private Description descriptionB;

    /**
     * @param descriptionA
     * @param descriptionB
     */
    public DistanceMeasure(Description descriptionA, Description descriptionB) {
        this("Distance", descriptionA, descriptionB);
    }
    
	/**
     * @param name the name of this measure
	 * @param descriptionA
	 * @param descriptionB
	 */
	public DistanceMeasure(String name, Description descriptionA, Description descriptionB) {
	    super(name);
		this.descriptionA = descriptionA;
		this.descriptionB = descriptionB;
	}

	@Override
	public Measurement measure(Structure structure) throws DescriptionException {
		Vector a = this.descriptionA.findStructureCenter(structure);
		Vector b = this.descriptionB.findStructureCenter(structure);
        
//        System.err.println("a " + a + " b " + b);
		
		double distance = Geometry.distance(a, b);
		return new Measurement(this.getName(), new Double(distance));
	}
    
    public String toString() {
        return "d (" + this.descriptionA.toPathString() + ", " + this.descriptionB.toPathString() + ") ";
    }

}
