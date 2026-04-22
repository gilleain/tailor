package tailor.cli;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.ParseException;

import tailor.datasource.PDBFileList;
import tailor.datasource.StructureSource;
import tailor.datasource.xml.XmlDescriptionReader;
import tailor.description.ChainDescription;

public class Matcher {
    
    private static ChainDescription read(String filename) {
        XmlDescriptionReader reader = new XmlDescriptionReader();
        return reader.readDescription(new File(filename));
    }
    
    public static void run(CommandLineHandler args) throws IOException {
    	ChainDescription description = null;
        if (args.getDescriptionFileName() != null) {
            description = read(args.getDescriptionFileName());
        }
        
        StructureSource structureSource = null;
        if (args.getStructureSourceFileName() != null) {
            structureSource = new PDBFileList(args.getStructureSourceFileName(), null);
        }
        
        // XXX TODO - error
        System.err.println("Matching " + args.getDescriptionFileName() + " to " + args.getStructureSourceFileName());
        if (description == null || structureSource == null) return;
        
        
        
        // TODO
//        Run run = new Run(description, structureSource);
//        Engine engine = EngineFactory.getEngine(description);
//        engine.run(run);
    }
    
    public static void main(String[] args) throws ParseException, IOException {
        System.err.println("Starting...");
        CommandLineHandler handler = new CommandLineHandler();
        Matcher.run(handler.processArguments(args));
    }

}
