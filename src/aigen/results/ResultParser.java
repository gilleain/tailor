package aigen.results;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import aigen.feature.Feature;
import aigen.feature.Structure;


/**
 * A module to convert text lists of results into Feature objects.
 * 
 * Use like:
 * 
 * Function<String, ExampleDescription> parser = line -> {
 *     String[] parts = line.split("\\s+");
 *     return new ExampleDescription(parts[0], parts[1], parts[2], parts[3], parts[4]);
 * };
 * 
 * for (Structure e : generateExamples(filename, pdbdir, parser)) {
 *     System.out.println(e);
 * }
 */
public class ResultParser {
    
    /**
     * Generates examples from a file using the provided parser function
     */
    public static Iterable<Structure> generateExamples(
            String filename, 
            String pdbdir, 
            Function<String, ExampleDescription> parseFunc) {
        
        return new Iterable<Structure>() {
            @Override
            public Iterator<Structure> iterator() {
                return new ExampleIterator(filename, pdbdir, parseFunc);
            }
        };
    }
    
    /**
     * Iterator that lazily generates examples
     */
    private static class ExampleIterator implements Iterator<Structure> {
        private Map<String, List<ExampleDescription>> examples;
        private Iterator<String> pdbidIterator;
        private ExampleStream exampleStream;
        private Iterator<Structure> currentExamples;
        private Structure nextExample;
        
        public ExampleIterator(String filename, String pdbdir, 
                             Function<String, ExampleDescription> parseFunc) {
            this.examples = new HashMap<>();
            
            // Read in the data from a file and convert to example descriptions
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        ExampleDescription exampleDescription = parseFunc.apply(line);
                        examples.computeIfAbsent(exampleDescription.getPdbid(), 
                            k -> new ArrayList<>()).add(exampleDescription);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
            
            // Initialize the example stream and iterator
            try {
                this.exampleStream = new ExampleStream(pdbdir);
            } catch (IOException e) {
                System.err.println("Error initializing PDB file list: " + e.getMessage());
                this.examples = new HashMap<>();
            }
            
            this.pdbidIterator = examples.keySet().iterator();
            this.currentExamples = null;
            advance();
        }
        
        private void advance() {
            while (true) {
                if (currentExamples != null && currentExamples.hasNext()) {
                    nextExample = currentExamples.next();
                    return;
                }
                
                if (pdbidIterator.hasNext()) {
                    String pdbid = pdbidIterator.next();
                    System.err.println(pdbid);
                    currentExamples = exampleStream.examples(pdbid, examples.get(pdbid));
                } else {
                    nextExample = null;
                    return;
                }
            }
        }
        
        @Override
        public boolean hasNext() {
            return nextExample != null;
        }
        
        @Override
        public Structure next() {
            Structure result = nextExample;
            advance();
            return result;
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java ResultParser <filename> <pdbdir>");
            System.exit(1);
        }
        
        String filename = args[0];
        String pdbdir = args[1];
        
        Function<String, ExampleDescription> parser = line -> {
            String[] parts = line.split("\\.");
            return new ExampleDescription(parts[0], parts[1], parts[2], parts[3], "0");
        };
        
        for (Structure e : generateExamples(filename, pdbdir, parser)) {
            recurse(e);
        }
    }
    
    /**
     * Recursively print the structure tree
     */
    private static void recurse(Feature tree) {
        System.out.println(tree.getClass().getSimpleName() + " " + tree);
        for (Feature child : tree) {
            recurse(child);
        }
    }
}