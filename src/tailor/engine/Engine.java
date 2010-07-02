package tailor.engine;

import java.util.ArrayList;

import tailor.datasource.StructureSource;
import tailor.description.ProteinDescription;
import tailor.measure.Measure;

/**
 * An engine matches a description to a structure source, and applies a list 
 * of measures to the results. 
 * 
 * @author maclean
 *
 */
public interface Engine {
    
    /**
     * @param run
     */
    public void run(Run run);

    /**
	 * Match the description to the structure source previously set, and
	 * apply the list of measures to the results.
	 * 
	 * @param description
	 * @param measures
	 */
	public void run(ProteinDescription description, ArrayList<Measure> measures);

    /**
     * Match the description to the structure source and apply the list of 
     * measures to the results.
     * 
     * @param description
     * @param measures
     * @param source
     */
    public void run(ProteinDescription description, 
                    ArrayList<Measure> measures,
                    StructureSource source);

}
