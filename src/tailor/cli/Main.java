package tailor.cli;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.ParseException;

import tailor.datasource.PDBFileList;
import tailor.datasource.StructureSource;
import tailor.datasource.XmlDescriptionReader;
import tailor.description.Description;
import tailor.engine.Engine;
import tailor.engine.EngineFactory;
import tailor.engine.Run;

public class Main {
    
    private static Description read(String filename) {
        XmlDescriptionReader reader = new XmlDescriptionReader();
        return reader.readDescription(new File(filename));
    }
    
    public static void run(CommandLineHandler commandLineHandler) throws IOException {
        Description description = null;
        if (commandLineHandler.getDescriptionFileName() != null) {
            description = read(commandLineHandler.getDescriptionFileName());
        }
        
        StructureSource structureSource = null;
        if (commandLineHandler.getStructureSourceFileName() != null) {
            structureSource = new PDBFileList(commandLineHandler.getStructureSourceFileName(), null);
        }
        
        // XXX TODO - error
        if (description == null || structureSource == null) return;
        
        Run run = new Run(description, structureSource);
        Engine engine = EngineFactory.getEngine(description);
        engine.run(run);
    }
    
    public static void main(String[] args) throws ParseException, IOException {
        CommandLineHandler handler = new CommandLineHandler();
        Main.run(handler.processArguments(args));
    }

}
