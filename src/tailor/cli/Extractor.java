package tailor.cli;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import tailor.source.PDBReader;
import tailor.source.PDBWriter;
import tailor.structure.Chain;
import tailor.structure.Group;
import tailor.structure.Protein;

public class Extractor {
    
    private class ArgHandler {
        private Options options;
        private String inputFilename;
        private String outputFilename;
        private int startResidueNumber;
        private int endResidueNumber;
        
        public ArgHandler() {
            options = new Options();
            options.addOption(opt("i", "filename", "Input filename"));
            options.addOption(opt("o", "filename", "Output filename"));
            options.addOption(opt("s", "start", "Start residue"));
            options.addOption(opt("e", "end", "End residue"));
        }
        
        public int getStartResidue() {
            return startResidueNumber;
        }
        
        public int getEndResidue() {
            return endResidueNumber;
        }
        
        public String getInputFilename() {
            return inputFilename;
        }
        
        public String getOutputFilename() {
            return outputFilename;
        }
        
        public ArgHandler processArguments(String[] args) throws ParseException {
            PosixParser parser = new PosixParser();
            CommandLine line = parser.parse(options, args, true);
            if (line.hasOption('i')) {
                inputFilename = line.getOptionValue('i');
            }
            
            if (line.hasOption('o')) {
                outputFilename = line.getOptionValue('o');
            }
            
            if (line.hasOption('s')) {
                startResidueNumber = Integer.parseInt(line.getOptionValue('s'));
            }
            
            if (line.hasOption('e')) {
                endResidueNumber = Integer.parseInt(line.getOptionValue('e'));
            }
            
            return this;
        }
        
        @SuppressWarnings("static-access")
        private Option opt(String o, String desc) {
            return OptionBuilder.withDescription(desc).create(o);
        }
        
        @SuppressWarnings("static-access")
        private Option opt(String o, String argName, String desc) {
            return OptionBuilder.hasArg()
                                .withDescription(desc)
                                .withArgName(argName)
                                .create(o);
        }
        
    }
    
    public static void extract(ArgHandler argHandler) throws IOException {
        File inputFile;
        if (argHandler.getInputFilename() != null) {
            inputFile = new File(argHandler.getInputFilename());
        } else {
            return; // TODO error message?
        }
        Protein protein = PDBReader.read(inputFile);
        Chain inputChain = protein.getChains().get(0);   // TODO : chains
        int start = argHandler.getStartResidue();
        int end = argHandler.getEndResidue();
        
        Chain outputChain = new Chain(inputChain.getName());
        for (Group group : inputChain.getGroups()) {
            int number = group.getNumber();
            if (number >= start && number <= end) {
                outputChain.addGroup(group);
            }
        }
        
        System.err.println("Extracting from " + argHandler.startResidueNumber 
                    + " to " + argHandler.endResidueNumber);
        
        Protein outputProtein = new Protein(protein.getName());
        outputProtein.addChain(outputChain);
        
        BufferedWriter out;
        if (argHandler.getOutputFilename() == null) {
            out = new BufferedWriter(new OutputStreamWriter(System.out));
        } else {
            out = new BufferedWriter(new FileWriter(new File(argHandler.getInputFilename())));
        }
        PDBWriter.write(outputProtein, out);
        out.flush();
    }

    public static void main(String[] args) throws ParseException, IOException {
        System.err.println("Starting...");
        Extractor.extract(new Extractor().new ArgHandler().processArguments(args));   
    }

}
