package tailor.engine;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import tailor.datasource.PDBFileList;
import tailor.datasource.Result;
import tailor.datasource.ResultsPrinter;
import tailor.datasource.StreamResultsPrinter;
import tailor.datasource.Structure;
import tailor.datasource.StructureSource;
import tailor.description.Description;
import tailor.description.DescriptionException;
import tailor.description.ProteinDescription;
import tailor.measure.Measure;
import tailor.measure.Measurement;

public abstract class AbstractBaseEngine implements Engine {
    
    /**
     * Results are printed to this printer.
     */
    private ResultsPrinter printer;
    
    /**
     * Errors are printed to this stream.
     */
    private PrintStream err;
    
    //  TODO : shouldn't this be a constructor arg?
    /**
     * The structures to be matched.
     */
    private StructureSource structureSource;    
    
    public AbstractBaseEngine() {
        this.printer = new StreamResultsPrinter(System.out);
        this.err = System.err;
    }
    
    public AbstractBaseEngine(String path) throws IOException {
        this(path, null);
    }
    
    public AbstractBaseEngine(
            String path, String[] filenames) throws IOException {
        this(new StreamResultsPrinter(System.out), System.err, path, filenames);
    }
    
    public AbstractBaseEngine(ResultsPrinter printer, PrintStream err, 
            StructureSource structureSource)  {
        this.printer = printer;
        this.err = err;
        this.structureSource = structureSource;
    }
    
    public AbstractBaseEngine(ResultsPrinter printer, PrintStream err, 
            String path) throws IOException {
        this(printer, err, path, null);
    }
    
    public AbstractBaseEngine(ResultsPrinter printer, PrintStream err, 
            String path, String[] filenames) throws IOException {
        this.printer = printer;
        this.err = err;
        this.structureSource = new PDBFileList(path, filenames);
    }
    
    @Override
    public void run(Run run) {
        if (this.structureSource == null) {
            this.structureSource = run.getStructureSource();
        }
        this.run(run.getDescription(), run.getMeasures());
    }

    @Override
    public void run(ProteinDescription description, ArrayList<Measure> measures) {
        this.run(description, measures, structureSource);
    }

    @Override
    public void run(ProteinDescription description,
            ArrayList<Measure> measures, StructureSource structureSource) {
        this.structureSource = structureSource;
        run((Description) description, measures);
    }

    public abstract List<Structure> match(
            Description description, Structure structure); 
    
    public void setPath(File path) throws IOException {
        String[] filenames = {};
        this.structureSource = new PDBFileList(path, filenames);
    }
    
    public void run(Description description, ArrayList<Measure> measures) {
        printer.printHeader(measures);
        
        while (this.structureSource.hasNext()) {
            try {
                Structure structure = structureSource.next();
                printer.signalNextStructure();
                
                for (Structure motif : this.match(description, structure)) {
                    
                    int i = 0;
                    int n = measures.size();
                    Measurement[] measurements = new Measurement[n];
                    for (Measure measure : measures) {
                        Measurement measurement = measure.measure(motif);
                        measurements[i++] = measurement;
                    }
                    
                    printer.printResult(
                            new Result(structure, motif, measurements));
                }
            } catch (IOException i) {
                err.println(i.toString());
            } catch (DescriptionException d) {
                err.println(d.toString());
                d.printStackTrace(err);
            }
        }
    }
}
