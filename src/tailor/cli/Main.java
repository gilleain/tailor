package tailor.cli;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.ParseException;

import tailor.datasource.PDBFileList;
import tailor.datasource.StructureSource;
import tailor.datasource.xml.XmlDescriptionReader;
import tailor.description.Description;
import tailor.engine.Engine;
import tailor.engine.EngineFactory;
import tailor.engine.Run;

public class Main {
    
    private static Description read(String filename) {
        XmlDescriptionReader reader = new XmlDescriptionReader();
        return reader.readDescription(new File(filename));
    }
    
    public static void run(CommandLineHandler args) throws IOException {
        Description description = null;
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
        
        
        Run run = new Run(description, structureSource);
        Engine engine = EngineFactory.getEngine(description);
        engine.run(run);
    }
    
    public static void main(String[] args) throws ParseException, IOException {
        System.err.println("Starting...");
        CommandLineHandler handler = new CommandLineHandler();
        Main.run(handler.processArguments(args));
    }

}
