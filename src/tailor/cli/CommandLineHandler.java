package tailor.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class CommandLineHandler {
    
    private Options options;
    
    private String descriptionFileName;
    
    private String structureSourceFileName;
    
    public CommandLineHandler() {
        options = new Options();
        options.addOption(opt("h", "Print help"));
        options.addOption(opt("d", "description", "Motif description"));
        options.addOption(opt("s", "source", "Structure source"));
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
            descriptionFileName = line.getOptionValue('d');
        }
        
        if (line.hasOption('s')) {
            structureSourceFileName = line.getOptionValue('s');   
        }
        
        return this;
    }

    private void printHelp() {
        System.err.println("Help");
    }

    public String getDescriptionFileName() {
        return descriptionFileName;
    }

    public String getStructureSourceFileName() {
        return structureSourceFileName;
    }

}
