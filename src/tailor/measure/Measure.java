package tailor.measure;

import tailor.datasource.Structure;
import tailor.description.DescriptionException;

public abstract class Measure {

	private String name;
	
	public abstract Measurement measure(Structure structure) throws DescriptionException;

    public Measure(String name) {
        this.name = name;
    }
    
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
}
