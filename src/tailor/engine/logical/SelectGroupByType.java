package tailor.engine.logical;

public class SelectGroupByType implements LogicalOperator {
	
	private String groupType;
	
	public SelectGroupByType(String groupType) {
		this.groupType = groupType;
	}
	
	public String getGroupType() {
		return this.groupType;
	}
	
	public String toString() {
		return this.getClass().getSimpleName() + "(" + groupType + ")";
	}

}
