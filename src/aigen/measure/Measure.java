package aigen.measure;

public interface Measure {
	
	abstract Object measure(Object target);

	abstract int getNumberOfColumns();

	abstract Object getColumnHeaders();

	abstract Object getFormatStrings();

}
