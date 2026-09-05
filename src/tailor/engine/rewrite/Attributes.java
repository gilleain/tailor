package tailor.engine.rewrite;

import java.util.List;
import java.util.Map;

/**
 * Properties of graph vertices and edges as a dictionary
 * of variables plus method names to values.
 */
public class Attributes {
	
	// TODO  bundle these into a record?
	private List<String> variables;
	private Map<String, String> variableToMethodMap;
	private Map<String, String> variableMethodToValueMap;

}
