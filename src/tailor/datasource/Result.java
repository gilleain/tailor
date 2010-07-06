package tailor.datasource;

import tailor.engine.Match;
import tailor.measure.Measurement;


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
    
//    public Result(
//            Structure structure, Structure motif, Measurement[] measurements) {
//        this.structureID = structure.getId();
//        this.motifData = motif.toString();
//        this.measurements = measurements;
//    }
    
    public Result(Match match, Measurement[] measurements) {
        // TODO - extract the structureId and 'motifData' from the Match
        this.match = match;
        this.measurements = measurements;
    }
    
    public int getNumberOfColumns() {
        return 2 + this.measurements.length;
    }
    
    public Object getValueAtColumn(int i) {
        try {
            switch (i) {
                case 0 : return this.structureID;
                case 1 : return this.motifData;
                default: return this.measurements[i - 2].getValue();
            }
        } catch (NullPointerException npe) {
            System.err.println("npe " + this);
            return "";
        }
    }
    
    public String toString() {
        String val = this.structureID + " " + this.motifData;
        for (Measurement measurement : this.measurements) {
        	try {
        		val += measurement.getValue() + " ";
        	} catch (NullPointerException npe) {
        		// XXX FIXME
        	}
        }
        return val;
    }
}
