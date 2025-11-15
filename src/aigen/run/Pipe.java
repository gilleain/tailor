package aigen.run;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import aigen.engine.Matcher;
import tailor.description.Description;
import tailor.description.DescriptionException;
import tailor.match.Match;
import tailor.measurement.Measure;
import tailor.measurement.Measurement;
import tailor.structure.Chain;
import tailor.structure.Protein;
import tailor.structure.Structure;

/**
 * A pipe that processes structures through a matcher and measures
 */
public class Pipe {
    private Description description;
    private Matcher matcher;
    private List<Measure> measures;
    private PrintStream out;
    
    private String columnSeparator;
    private String lineSeparator;
    private int numberOfColumns;
    private String resultTemplate;
    
    public Pipe(Description description, List<Measure> measures) {
        this(description, measures, System.out);
    }
    
    public Pipe(Description description, List<Measure> measures, PrintStream out) {
        this.description = description;
        this.matcher = new Matcher(description);
        this.measures = measures;
        this.out = out;
        
        this.columnSeparator = "\t";
        this.lineSeparator = System.lineSeparator();
        
        // Calculate number of columns
        this.numberOfColumns = 0;
        for (Measure measure : measures) {
            this.numberOfColumns += measure.getNumberOfColumns();
        }
        
        // Build format string template
        List<String> formatStrings = new ArrayList<>();
        for (Measure measure : measures) {
            String[] formatStringValues = measure.getFormatStrings();
            for (String formatString : formatStringValues) {
            	formatStrings.add(formatString);
            }
        }
        this.resultTemplate = String.join(this.columnSeparator, formatStrings);
    }
    

	public Description getDescription() {
        return description;
    }
    
    /**
     * Writes the header line
     */
    public void writeHeader() {
        String headerLine = getHeader();
        this.out.print(headerLine);
    }
    
    /**
     * Generates the header line
     */
    public String getHeader() {
        String initialHeader = "pdbid" + this.columnSeparator + "motif";
        
        List<String> measureHeaderList = new ArrayList<>();
        for (Measure measure : this.measures) {
            String[] columnHeaders = measure.getColumnHeaders();
            for (String header : columnHeaders) {
            	measureHeaderList.add(header);
            }
        }
        
        String measureHeader = String.join(this.columnSeparator, measureHeaderList);
        return initialHeader + this.columnSeparator + measureHeader + this.lineSeparator;
    }
    
    /**
     * Formats a motif as a string
     */
    public String formatMotif(Structure structure, List<Structure> motif) {
        try {
            // Get the first model
//        	Structure model = motif.get(0);
        	Protein protein = (Protein) motif.get(0);	// TODO
            
            // Format chain info
            List<String> chainInfos = new ArrayList<>();
            for (Chain chain : protein.getChains()) {
                chainInfos.add(chain.getName() + " " + chain.getResidueRange());
            }
            
            String motifStr = String.join(" ", chainInfos);
            return structure.toString() + this.columnSeparator + motifStr;
        } catch (Exception e) {
            return motif.toString();
        }
    }
    
    /**
     * Formats the complete results line
     */
    public String formatResults(Structure structure, Structure motif, List<Measurement> results) {
        String signature = formatMotif(structure, List.of(motif));	/// XXX - why a list here?
        String resultStr;
        
        // TODO - do this in a better way ...
        try {
            // Convert results to array for String.format
            Object[] resultArray = results.toArray();
            resultStr = String.format(this.resultTemplate, resultArray);
        } catch (Exception e) {
            // Fallback: just join the results as strings
            resultStr = results.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));
        }
        
        return signature + this.columnSeparator + resultStr + this.lineSeparator;
    }
    
    /**
     * Runs the pipe on a structure
     */
    public void run(Structure structure) throws DescriptionException {
        for (Structure fragment : this.matcher.findAll(structure)) {
            List<Measurement> results = new ArrayList<>();
            
            for (Measure measure : this.measures) {
            	// XXX TODO - fix this
            	Match match = null;	// should wrap the fragment
            	Measurement result = measure.measure(match);
            	results.add(result);
            }
            
            this.out.print(formatResults(structure, fragment, results));
        }
    }
}
