package tailor.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tailor.api.Operator;
import tailor.api.ResultsPrinter;
import tailor.api.Sink;
import tailor.datasource.StructureSource;
import tailor.description.ChainDescription;
import tailor.engine.operator.GroupSource;
import tailor.engine.plan.Plan;
import tailor.engine.plan.Planner;
import tailor.engine.plan.Result;
import tailor.structure.Chain;
import tailor.structure.Structure;

public class PlanEngine implements Engine {
	
	private Run run;

	private Plan plan;
	
	private ResultsPrinter resultsPrinter;	// TODO
	
	public PlanEngine(Run run, ResultsPrinter resultsPrinter) {
		this.run = run;
		this.plan = makePlan(run);
		this.resultsPrinter = resultsPrinter;
	}
	
	private Plan makePlan(Run run) {
		ChainDescription chainDescription = run.getDescriptions().get(0);	// TODO
		return new Planner().plan(chainDescription);
	}

	@Override
	public void run() {
		StructureSource structureSource = run.getStructureSource();
		while (structureSource.hasNext()) {
			try {
				Structure str = structureSource.next();
				for (Structure subStr : str.getSubstructures()) { // TODO ugh
					Chain chain = (Chain) subStr;
					runChain(chain); // TODO - want to be cleverer with handling chains
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void runChain(Chain chain) {
		System.err.println("Running chain " + chain.getName());
		List<Sink<Result>> inputs = plan.getInputPipes();
		GroupSource groupSource = new GroupSource(chain, inputs);
		List<Operator> fullPipeline = new ArrayList<>();
		fullPipeline.add(groupSource);
		fullPipeline.addAll(plan.getOperators());

		for (Operator operator : fullPipeline) {
			operator.run();
		}
	}

}
