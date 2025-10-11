package aigen.condition;

public interface Condition {

	boolean satisfiedBy(Object feature);

	void negate();

	boolean isNegated();

	public boolean hasProperty(String key);

	public Object getProperty(String key);
}
