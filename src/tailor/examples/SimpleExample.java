package tailor.examples;

import tailor.description.DescriptionFactory;
import tailor.engine.BasicEngine;
import tailor.engine.Engine;
import tailor.engine.Run;

public class SimpleExample {

	public static void main(String[] args) {

		String filename = args[0];
		
		DescriptionFactory factory = new DescriptionFactory();
		factory.addResidues(4);
        factory.createHBondCondition(3.5, 90.0, 90.0, 3, 0);
		
        Run run = new Run(filename);
        run.addMeasure(factory.createPhiMeasure("psi2", 2));
        run.addMeasure(factory.createPsiMeasure("phi2", 2));
        
        run.addDescription(factory.getProduct());
		
        Engine engine = new BasicEngine();
        engine.run(run);
	}

}
