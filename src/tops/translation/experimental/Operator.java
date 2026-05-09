package tops.translation.experimental;

public interface Operator {
	
	void run();

	String getId();
	
	void setId(String id);

	void setOutput(Pipe output);

}
