package tailor.cli;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.ParseException;

import tailor.datasource.xml.XmlDescriptionReader;
import tailor.description.ChainDescription;
import tailor.engine.Engine;
import tailor.engine.EngineFactory;
import tailor.engine.EngineFactory.EngineType;
import tailor.engine.Run;
import tailor.engine.SysoutResultsPrinter;

public class Matcher {
    
    private static ChainDescription read(String filename) {
        XmlDescriptionReader reader = new XmlDescriptionReader();
        return reader.readDescription(new File(filename));
    }
    
    public static void run(CommandLineHandler args) throws IOException {
    	ChainDescription chainDescription = null;
        if (args.getDescriptionFileName() != null) {
        	chainDescription = read(args.getDescriptionFileName());
        }
        
        String path = null;
        if (args.getStructureSourceFileName() != null) {	
            path = args.getStructureSourceFileName();
        }
        
        // XXX TODO - error
        System.err.println("Matching " + args.getDescriptionFileName() + " to " + args.getStructureSourceFileName());
        if (chainDescription == null || path == null) return;
        
        Run run = new Run(chainDescription, path);
        Engine engine = EngineFactory.getEngine(run, new SysoutResultsPrinter(), EngineType.PLAN);
        engine.run();
    }
    
    public static void main(String[] args) throws ParseException, IOException {
        System.err.println("Starting...");
        CommandLineHandler handler = new CommandLineHandler();
        Matcher.run(handler.processArguments(args));
    }

}
