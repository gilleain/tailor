package tailor.engine.logical;

import tailor.structure.PolymerType;

public class SelectChainByType implements LogicalOperator {
	
	private PolymerType type;

	public SelectChainByType(PolymerType type) {
		this.type = type;
	}
	
	public PolymerType getType() {
		return this.type;
	}
	
	public String toString() {
		return this.getClass().getSimpleName() + "(" + type.toString() + ")";
	}

}
