package aigen.condition;

abstract class BaseCondition implements Condition {
	protected boolean negated = false;	// TODO - could just use java.util.Function instead?
	protected String name;

	@Override
	public void negate() {
		this.negated = true;
		this.name = "!" + this.name;
	}

	@Override
	public boolean isNegated() {
		return negated;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean hasProperty(String key) {
		return false;	// TODO
	}

	public Object getProperty(String key) {
		return null;	// TODO
	}
}
