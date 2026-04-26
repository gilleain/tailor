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
import tailor.engine.operator.ResultsPrinterAdapter;
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
		System.err.println("Plan "); plan.describe();
		this.resultsPrinter = resultsPrinter;
	}
	
	private Plan makePlan(Run run) {
		ChainDescription chainDescription = getChainDescription();
		return new Planner().plan(chainDescription, false);
	}
	
	private ChainDescription getChainDescription() {
		return run.getDescriptions().get(0);	// TODO
	}

	@Override
	public void run() {
		StructureSource structureSource = run.getStructureSource();
		while (structureSource.hasNext()) {
			try {
				Structure str = structureSource.next();
				for (Structure subStr : str.getSubstructures()) { // TODO ugh
					Chain chain = (Chain) subStr;
					runChain(str.getName(), chain); // TODO - want to be cleverer with handling chains
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void runChain(String structureName, Chain chain) {
		plan = makePlan(run);	// TODO - why is this necessary?
		System.err.println("Running chain " + structureName + " " + chain.getName());
		List<Sink<Result>> inputs = plan.getInputPipes();
		GroupSource groupSource = new GroupSource(chain, inputs);
		List<Operator> fullPipeline = new ArrayList<>();
		fullPipeline.add(groupSource);
		fullPipeline.addAll(plan.getOperators());
		Operator resOperator = new ResultsPrinterAdapter(resultsPrinter, plan.getOutputPoint(), getChainDescription());
		fullPipeline.add(resOperator);

		for (Operator operator : fullPipeline) {
			operator.run();
		}
		plan.clear();
	}

}
