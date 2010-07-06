package tailor.measure;

import tailor.description.DescriptionException;
import tailor.engine.Match;

/**
 * A measure is like a ruler - the instrument that you use to make the 
 * measurement. In terms of code, a measure is applied to a Match between
 * a Description and a Structure.
 * 
 * @author maclean
 *
 */
public abstract class Measure {

	/**
	 * An identifier for the particular instance of the Measure, used for
	 * labelling columns of data, for example
	 */
	private String name;
	
	public Measure(String name) {
	    this.name = name;
	}
	
	/**
	 * Perform the measurement on the match between a description and a piece
	 * of structure.
	 * 
	 * @param match an association of a structure and a description 
	 * @return a Measurement object with the value
	 * @throws DescriptionException
	 */
	public abstract Measurement measure(Match match) throws DescriptionException;
    
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
}
