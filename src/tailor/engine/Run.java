package tailor.engine;

import java.io.File;
import java.io.IOException;
import java.util.List;

import tailor.datasource.PDBFileList;
import tailor.datasource.StructureSource;
import tailor.description.Description;
import tailor.description.ProteinDescription;
import tailor.measure.Measure;

public class Run {

	private Description description;
	private StructureSource structureSource;
    
    public Run() {
        // TODO : check this before running! - perhaps the empty constructor is a bad idea
        this.description = null;
        this.structureSource = null;
    }
    
    public Run(String path) {
    	this();
    	try {
    		this.structureSource = new PDBFileList(path, null);
    	} catch (IOException e) {
    		// FIXME!
    	}
    }
	
	public Run(ProteinDescription description, String path) throws IOException {
		this.description = description;
		String[] filenames = {};
		this.structureSource = new PDBFileList(path, filenames);
	}
	
	public Run(Description description, StructureSource structureSource) throws IOException {
	    this.description = description;
	    this.structureSource = structureSource;
	}
	
	public Run(Description description, List<Measure> measures, String path, String[] filenames) throws IOException {
		this.description = description;
		this.structureSource = new PDBFileList(path, filenames);
	}
	
	public StructureSource getStructureSource() {
		return this.structureSource;
	}
    
    public void addDescription(ProteinDescription description) {
    	this.description = description;	// TODO : convert to adding to a list...
    }
    
    public Description getDescription() {
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
