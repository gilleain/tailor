package tailor.experiment.condition;

import java.util.logging.Logger;

import tailor.experiment.api.AtomListCondition;

public abstract class AbstractAtomListCondition implements AtomListCondition {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public String op(double a, double b) {
		return a < b? " < " : " > ";
	}

}
