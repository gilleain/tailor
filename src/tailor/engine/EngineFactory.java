package tailor.engine;

import tailor.api.ResultsPrinter;

public class EngineFactory {
	
	public static enum EngineType {
		PLAN,		// Planner-based engine
		MSD			// TODO - the MSD motif engine?
	};
	
	public static Engine getEngine(Run run, ResultsPrinter resultsPrinter, EngineType engineType) {
		if (engineType == EngineType.PLAN) {
			return new PlanEngine(run, resultsPrinter);
		} else {
			throw new IllegalStateException("Unknonwn engine type " + engineType);
		}
	}
}
