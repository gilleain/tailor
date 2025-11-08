package aigen.run;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import aigen.description.Description;
import aigen.description.DescriptionException;
import aigen.engine.Matcher;
import aigen.feature.Chain;
import aigen.feature.Feature;
import aigen.feature.Model;
import aigen.feature.Structure;
import aigen.measure.Measure;

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
            Object formatStringValue = measure.getFormatStrings();
            if (formatStringValue instanceof List) {
                formatStrings.addAll((List<String>) formatStringValue);
            } else {
                formatStrings.add((String) formatStringValue);
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
            Object columnHeaders = measure.getColumnHeaders();
            if (columnHeaders instanceof List) {
                measureHeaderList.addAll((List<String>) columnHeaders);
            } else if (columnHeaders instanceof String[]) {
                for (String header : (String[]) columnHeaders) {
                    measureHeaderList.add(header);
                }
            } else {
                measureHeaderList.add((String) columnHeaders);
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
        	Structure model = motif.get(0);
            
            // Format chain info
            List<String> chainInfos = new ArrayList<>();
            for (Feature chainFeature : model) {
            	Chain chain = (Chain) chainFeature;
                chainInfos.add(chain.getChainID() + " " + chain.getResidueRange());
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
    public String formatResults(Structure structure, Structure motif, List<Object> results) {
        String signature = formatMotif(structure, List.of(motif));	/// XXX - why a list here?
        String resultStr;
        
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
            List<Object> results = new ArrayList<>();
            
            for (Measure measure : this.measures) {
                Object result = measure.measure(fragment);
                
                if (result instanceof List) {
                    results.addAll((List<?>) result);
                } else if (result instanceof Object[]) {
                    for (Object r : (Object[]) result) {
                        results.add(r);
                    }
                } else {
                    results.add(result);
                }
            }
            
            this.out.print(formatResults(structure, fragment, results));
        }
    }
}
