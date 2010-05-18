package tailor.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import tailor.datasource.PDBFileList;
import tailor.datasource.StructureSource;
import tailor.description.ProteinDescription;
import tailor.measure.Measure;

public class Run {

	private ProteinDescription description;
	private StructureSource structureSource;
	private ArrayList<Measure> measures;
    
    public Run() {
        // TODO : check this before running! - perhaps the empty constructor is a bad idea
        this.description = null;
        this.structureSource = null;
        this.measures = new ArrayList<Measure>();
    }
    
    public Run(String path) {
    	this();
    	try {
    		this.structureSource = new PDBFileList(path, null);
    	} catch (IOException e) {
    		// FIXME!
    	}
    }
	
	public Run(ProteinDescription description, ArrayList<Measure> measures, String path) throws IOException {
		this.description = description;
		String[] filenames = {};
		this.structureSource = new PDBFileList(path, filenames);
		this.measures = measures;
	}
	
	public Run(ProteinDescription description, ArrayList<Measure> measures, String path, String[] filenames) throws IOException {
		this.description = description;
		this.structureSource = new PDBFileList(path, filenames);
		this.measures = measures;
	}
	
	public StructureSource getStructureSource() {
		return this.structureSource;
	}
    
    public void addMeasure(Measure measure) {
        this.measures.add(measure);
    }
    
    public ArrayList<Measure> getMeasures() {
    	return this.measures;
    }
    
    public void addDescription(ProteinDescription description) {
    	this.description = description;	// TODO : convert to adding to a list...
    }
    
    public ProteinDescription getDescription() {
    	return this.description;		// TODO : convert to getting from a list
    }
    
    public void setDescription(ProteinDescription description) {
        this.description = description;
    }

    public void setPath(File path) throws IOException {
        String[] filenames = {};
        // FIXME : give PDBFileList some constructors for handling File s
        this.structureSource = new PDBFileList(path.toString(), filenames);
    }
 
    public String toString() {
        return String.format("Description '%s' vs %s structures", this.description.toString(), this.structureSource.size());
    }
}
