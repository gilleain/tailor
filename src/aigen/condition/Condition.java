package aigen.condition;

interface Condition {
	
	boolean satisfiedBy(Object feature);

	void negate();

	boolean isNegated();
}
