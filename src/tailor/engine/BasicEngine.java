package tailor.engine;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import tailor.Level;
import tailor.datasource.StreamResultsPrinter;
import tailor.datasource.PDBFileList;
import tailor.datasource.Result;
import tailor.datasource.ResultsPrinter;
import tailor.datasource.Structure;
import tailor.datasource.StructureSource;
import tailor.description.ChainDescription;
import tailor.description.DescriptionException;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;
import tailor.measure.Measure;
import tailor.measure.Measurement;

/**
 * @author maclean
 *
 */
public class BasicEngine implements Engine, Runnable {
	
	private ResultsPrinter printer;
	private PrintStream err;
	private StructureSource structureSource;	// TODO : shouldn't this be a constructor arg?
	
	private Run run;	// XXX tmp hack for swing threads
	
	public BasicEngine() {
		this.printer = new StreamResultsPrinter(System.out);
		this.err = System.err;
	}
	
	public BasicEngine(String path) throws IOException {
		this(path, null);
	}
	
	public BasicEngine(String path, String[] filenames) throws IOException {
		this(new StreamResultsPrinter(System.out), System.err, path, filenames);
	}
	
	public BasicEngine(ResultsPrinter printer, PrintStream err, 
			StructureSource structureSource)  {
		this.printer = printer;
		this.err = err;
		this.structureSource = structureSource;
	}
	
	public BasicEngine(ResultsPrinter printer, PrintStream err, String path) throws IOException {
		this(printer, err, path, null);
	}
	
	public BasicEngine(ResultsPrinter printer, PrintStream err, String path, String[] filenames) throws IOException {
		this.printer = printer;
		this.err = err;
		this.structureSource = new PDBFileList(path, filenames);
	}
	
	public void setPath(File path) throws IOException {
		String[] filenames = {};
		this.structureSource = new PDBFileList(path, filenames);
	}
	
	public void setRun(Run run) {
		this.run = run;
	}
	
	public void run() {
		this.run(this.run);
	}
	
	public void run(Run run) {
		if (this.structureSource == null) {
			this.structureSource = run.getStructureSource();
		}
		this.run(run.getDescription(), run.getMeasures());
	}
	
	public void run(ProteinDescription description, ArrayList<Measure> measures) {
		printer.printHeader(measures);
		
        while (this.structureSource.hasNext()) {
			try {
                Structure structure = structureSource.next();
                printer.signalNextStructure();
                
				for (Structure motif : this.scan(description, structure)) {
					
					int i = 0;
					Measurement[] measurements = new Measurement[measures.size()];
					for (Measure measure : measures) {
						Measurement measurement = measure.measure(motif);
						measurements[i++] = measurement;
					}
					
					printer.printResult(new Result(structure, motif, measurements));
				}
			} catch (IOException i) {
				err.println(i.toString());
			} catch (DescriptionException d) {
				err.println(d.toString());
                d.printStackTrace(err);
			}
		}
	}
	
    public void run(ProteinDescription description,
            ArrayList<Measure> measures, StructureSource source) {
        // TODO Auto-generated method stub
        
    }

    public ArrayList<Structure> scan(ProteinDescription description, Structure protein) {
		ArrayList<Structure> matches = new ArrayList<Structure>();
        for (ChainDescription chainDescription : description.getChainDescriptions()) {
            for (Structure chain : protein.getSubStructures()) {
            	
                int n = 0;	// XXX TMP
                
                for (Structure chainMatch : this.scan(chainDescription, chain)) {
                    Structure motif = new Structure(Level.PROTEIN);
                    motif.setProperty("Name", protein.getId());
                    motif.addSubStructure(chainMatch);
                    matches.add(motif);
                    
                    n++;	// XXX TMP
                    
                }
                
                System.err.println("scanned chain " + chain.getId() + " - " + n + " matches");
            }
        }
        
        return matches;
	}
	
	public ArrayList<Structure> scan(ChainDescription description, Structure chain) {
		ArrayList<Structure> matches = new ArrayList<Structure>();

		int span = description.size();

		ArrayList<Structure> groups = chain.getSubStructures();
		int lastPossibleStart = groups.size() - span;
		for (int start = 0; start < lastPossibleStart; start++) {
			Structure chainMatch = this.scan(description, groups, start);

			// only if we get a match of sufficient size
			// is it worthwhile to consider any conditions
			if (chainMatch.size() == description.size()) {
				if (description.conditionsSatisfied(chainMatch)) {
					chainMatch.setProperty("Name", chain.getProperty("Name"));
					matches.add(chainMatch);
				}
			}
		}

		return matches;
	}
	
	public Structure scan(ChainDescription description, 
	        ArrayList<Structure> groups, int start) {
		 Structure chain = new Structure(Level.CHAIN);
		 for (int index = 0; index < description.size(); index++) {
		     GroupDescription groupDescription = 
		         description.getGroupDescription(index);
			 Structure group = groups.get(start + index);
			 if (groupDescription.nameMatches(group)) {

				 // returns a new group filled with 
			     // as many matching atoms as possible
				 Structure groupMatch = groupDescription.matchTo(group);
				 
				 // only if we get a match of sufficient size
				 // is it worthwhile to consider any conditions
				 if (groupMatch.size() == groupDescription.size()) {
					 if (groupDescription.conditionsSatisfied(groupMatch)) {
						 groupMatch.setProperty(
						         "Name", group.getProperty("Name"));
						 groupMatch.setProperty(
						         "Number", group.getProperty("Number"));
						 chain.addSubStructure(groupMatch);
					 }
				 }
			 } else {
				 return chain;
			 }
		 }
		 return chain;
	 }
	 
}
