package tailor.cli;

import java.io.File;

import org.apache.commons.cli.ParseException;

import tailor.datasource.xml.XmlDescriptionReader;
import tailor.description.ChainDescription;
import tailor.engine.plan.Plan;
import tailor.engine.plan.Planner;
import tailor.view.PlanFrame;

public class PlanViewer {
	
	public static void main(String[] args) throws ParseException {
		CommandLineHandler handler = new CommandLineHandler().processArguments(args);
		
		ChainDescription chainDescription = null;
        if (handler.getDescriptionFileName() != null) {
        	chainDescription = read(handler.getDescriptionFileName());
        }
       
        Plan plan = new Planner().plan(chainDescription);
        plan.describe();
		PlanFrame.show(plan);
	}
	
	private static ChainDescription read(String filename) {
        XmlDescriptionReader reader = new XmlDescriptionReader();
        return reader.readDescription(new File(filename));
    }

}
