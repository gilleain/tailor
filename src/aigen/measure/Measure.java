package aigen.measure;

abstract class Measure {
	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public abstract Object measure(Object target);

	public abstract int getNumberOfColumns();

	public abstract Object getColumnHeaders();

	public abstract Object getFormatStrings();
}
