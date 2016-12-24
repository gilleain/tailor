package tailor.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class CommandLineHandler {
    
    private Options options;
    
    private String descriptionFileName;
    
    private String structureSourceFileName;
    
    public CommandLineHandler() {
        options = new Options();
    }
    
    public CommandLineHandler processArguments(String[] args) throws ParseException {
        PosixParser parser = new PosixParser();
        CommandLine line = parser.parse(options, args, true);
        
        if (line.hasOption('d')) {
            descriptionFileName = line.getOptionValue('d');
        }
        
        if (line.hasOption('s')) {
            structureSourceFileName = line.getOptionValue('s');   
        }
        
        return this;
    }

    public String getDescriptionFileName() {
        return descriptionFileName;
    }

    public String getStructureSourceFileName() {
        return structureSourceFileName;
    }

}
