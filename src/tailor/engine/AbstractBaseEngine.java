package tailor.engine;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import tailor.datasource.PDBFileList;
import tailor.datasource.Result;
import tailor.datasource.ResultsPrinter;
import tailor.datasource.StreamResultsPrinter;
import tailor.datasource.StructureSource;
import tailor.description.Description;
import tailor.match.Match;
import tailor.measurement.Measure;
import tailor.measurement.Measurement;
import tailor.structure.Structure;

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
    
    // TMP
    private Run run;
    
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
    
    public void setRun(Run run) {
        this.run = run;
    }
    
    public void run() {
        run(run);
    }
    
    @Override
    public void run(Run run) {
        if (this.structureSource == null) {
            this.structureSource = run.getStructureSource();
        }
        for (Description description : run.getDescriptions()) {
            this.runDescription(description);
        }
    }

    @Override
    public void run(Description description, StructureSource structureSource) {
        this.structureSource = structureSource;
        runDescription(description);
    }

    public abstract List<Match> match(Description description, Structure structure); 
    
    @Override
    public void runDescription(Description description) {
        List<Measure<? extends Measurement>> measures = description.getMeasures();
        printer.printHeader(measures);
        
        while (structureSource.hasNext()) {
            try {
                Structure structure = structureSource.next();
                printer.signalNextStructure();
                
                for (Match match : this.match(description, structure)) {
                    
                    int i = 0;
                    int n = measures.size();
                    Measurement[] measurements = new Measurement[n];
                    for (Measure<? extends Measurement> measure : measures) {
                        Measurement measurement = measure.measure(match);
                        measurements[i++] = measurement;
                    }
                    
                    printer.printResult(new Result(match, measurements));
                }
            } catch (IOException i) {
                err.println(i.toString());
            }
//            } catch (DescriptionException d) {
//                err.println(d.toString());
//                d.printStackTrace(err);
//            }
        }
    }
}
