package translation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tops.translation.model.Domain;

/**
 * Simple class to wrap a map from chain strings to lists of domains.
 * 
 * @author maclean
 *
 */
public class ChainDomainMap implements Iterable<String> {
	
	private Map<String, List<Domain>> data;
	
	public ChainDomainMap() {
		data = new HashMap<String, List<Domain>>();
	}

	@Override
	public Iterator<String> iterator() {
		return data.keySet().iterator();
	}
	
	public List<Domain> get(String id) {
		return data.get(id);
	}
	
	public void put(String id, List<Domain> domains) {
		data.put(id, domains);
	}
	
	public boolean containsKey(String id) {
		return data.containsKey(id);
	}
}
