package tailor.measure;

/**
 * @author maclean
 *
 * A Measurement is the result of using a Measure.
 */
public class Measurement {

	private String name;
	private Object value;
	
	/**
	 * @param name
	 * @param value
	 */
	public Measurement(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getValue() {
//		return String.valueOf(this.value);  // FIXME this is more general
        return String.format("%.2f", this.value);
	}
	
	public String toString() {
		return String.format("%s = %s", this.name, this.value);
	}
}
