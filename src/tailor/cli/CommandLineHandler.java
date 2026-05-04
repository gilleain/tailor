package tailor.cli;

import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class CommandLineHandler {
    
    private Options options;
    
    private Optional<String> descriptionFileName;
    
    private Optional<String> structureSourceFileName;
    
    private Optional<String> resultsFileName;
    
    public CommandLineHandler() {
        options = new Options();
        options.addOption(opt("h", "Print help"));
        options.addOption(opt("d", "description", "Motif description"));
        options.addOption(opt("s", "source", "Structure source"));
        options.addOption(opt("r", "results", "Results file"));
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
    
    public CommandLineHandler processArguments(String[] args) throws ParseException {
        PosixParser parser = new PosixParser();
        CommandLine line = parser.parse(options, args, true);
        
        if (line.hasOption('h')) {
            printHelp();
            return this;
        }
        
        if (line.hasOption('d')) {
            descriptionFileName = Optional.of(line.getOptionValue('d'));
        } else {
        	descriptionFileName = Optional.empty();
        }
        
        if (line.hasOption('s')) {
            structureSourceFileName = Optional.of(line.getOptionValue('s'));   
        } else {
        	structureSourceFileName = Optional.empty();
        }
        
        if (line.hasOption('r')) {
            resultsFileName = Optional.of(line.getOptionValue('r'));
        } else {
        	resultsFileName = Optional.empty();
        }
        
        return this;
    }

    private void printHelp() {
        System.err.println("Help");
    }

    public Optional<String> getDescriptionFileName() {
        return descriptionFileName;
    }

    public Optional<String> getStructureSourceFileName() {
        return structureSourceFileName;
    }
    
    public Optional<String> getResultsFileName() {
        return resultsFileName;
    }

}
