package tailor.category;

class Row {	// TODO - could be record
	public double[] values;
	public String id; // TODO
	public String startEnd;	// TODO
	public Row(String id, String startEnd, double[] values) {
		this.id = id;
		this.startEnd = startEnd;
		this.values = values;
	}
}