package tailor.datasource;

import tailor.measure.Measurement;


/**
 * Semi-temporary class to hold the results of a filesystem run
 * to be passed to a TableModel that will be used by the gui. 
 * 
 * @author maclean
 *
 */
public class Result {
    
    private String structureID;
    private String motifData;
    private Measurement[] measurements;
    
    public Result(Structure structure, Structure motif, Measurement[] measurements) {
        this.structureID = structure.getId();
        this.motifData = motif.toString();
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
