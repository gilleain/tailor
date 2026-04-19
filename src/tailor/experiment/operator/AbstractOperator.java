package tailor.experiment.operator;

import tailor.api.Operator;

public abstract class AbstractOperator implements Operator {
	
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
