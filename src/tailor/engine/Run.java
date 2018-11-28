package tailor.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tailor.datasource.PDBFileList;
import tailor.datasource.StructureSource;
import tailor.description.Description;
import tailor.description.ProteinDescription;

public class Run {

	private List<Description> descriptions;
	
	private StructureSource structureSource;
    
    public Run() {
        this.descriptions = new ArrayList<>();
        this.structureSource = null;
    }
    
    public Run(String path) {
    	this();
    	try {
    		this.structureSource = new PDBFileList(path);
    	} catch (IOException e) {
    		// FIXME!
    	}
    }
	
	public Run(ProteinDescription description, String path) throws IOException {
		this(description, new PDBFileList(path));
	}
	
	public Run(Description description, StructureSource structureSource) throws IOException {
	    this.descriptions.add(description);
	    this.structureSource = structureSource;
	}
	
	public int getMeasureCount() {
	    int count = 0;
	    for (Description description : this.descriptions) {
	        count += description.getMeasures().size();
	    }
	    return count;
	}
	
	public StructureSource getStructureSource() {
		return this.structureSource;
	}
    
    public void addDescription(ProteinDescription description) {
    	this.descriptions.add(description);
    }
    
    public List<Description> getDescriptions() {
    	return this.descriptions;
    }

    public void setPath(File path) throws IOException {
        this.structureSource = new PDBFileList(path);
    }
 
    public String toString() {
        return String.format("Description '%s' vs %s structures", this.descriptions.toString(), this.structureSource.size());
    }
}
