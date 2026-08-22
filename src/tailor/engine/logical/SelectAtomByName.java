package tailor.engine.logical;

public class SelectAtomByName implements LogicalOperator {
	
	private String atomName;
	
	public SelectAtomByName(String atomName) {
		this.atomName = atomName;
	}
	
	public String toString() {
		return this.getClass().getSimpleName() + "(" + atomName + ")";
	}

}
