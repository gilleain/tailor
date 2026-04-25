package tailor.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tailor.datasource.PDBFileList;
import tailor.datasource.StructureSource;
import tailor.description.ChainDescription;

public class Run {

	// TODO - potentially allow running both ProteinDescriptions and ChainDescriptions?
	private List<ChainDescription> descriptions;	
	
	
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
	
	public Run(ChainDescription description, String path) throws IOException {
		this(description, new PDBFileList(path));
	}
	
	public Run(ChainDescription description, StructureSource structureSource) throws IOException {
		this.descriptions = new ArrayList<>();
	    this.descriptions.add(description);
	    this.structureSource = structureSource;
	}
	
	
	public int getMeasureCount() {
	    int count = 0;
	    for (ChainDescription description : this.descriptions) {
	        count += description.getAtomListMeasures().size();
	    }
	    return count;
	}
	
	public StructureSource getStructureSource() {
		return this.structureSource;
	}
    
    public List<ChainDescription> getDescriptions() {
    	return this.descriptions;
    }

    public void setPath(File path) throws IOException {
        this.structureSource = new PDBFileList(path);
    }
 
    public String toString() {
        return String.format("Description '%s' vs %s structures", this.descriptions.toString(), this.structureSource.size());
    }
}
