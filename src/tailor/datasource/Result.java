package tailor.datasource;

import tailor.match.Match;
import tailor.measurement.Measurement;


/**
 * Combines the Match - which is the association between the Description and
 * the Structure - with the list of Measurements made.
 * 
 * @author maclean
 *
 */
public class Result {
    
    private Match match;
    
    private String structureID;
    
    private String motifData;
    
    private Measurement[] measurements;
    
    public Result(Match match, Measurement[] measurements) {
        this.match = match;
        this.measurements = measurements;
        
        // XXX this only works if the match is at the protein level, I guess 
        this.structureID = match.getStructure().getProperty("Name");
        this.motifData = match.getStructure().toString();
    }
    
    public int getNumberOfColumns() {
        return 2 + this.measurements.length;
    }
    
    public Object getValueAtColumn(int i) {
        try {
            switch (i) {
                case 0 : return this.structureID;
                case 1 : return this.motifData;
                // XXX FIXME
                default: return this.measurements[i - 2].toString();   
            }
        } catch (NullPointerException npe) {
            System.err.println("npe " + this);
            return "";
        }
    }
    
    public String toString() {
        String val = this.structureID + " " + this.motifData + "[";
        for (Measurement measurement : this.measurements) {
        	try {
        		val += measurement.toString() + " ";
        	} catch (NullPointerException npe) {
        		// XXX FIXME
        	}
        }
        return val + "]";
    }
}
