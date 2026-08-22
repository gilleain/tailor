package tailor.engine.physical;

import java.util.List;

import tailor.structure.Chain;

public class ChainPipe {
	
	private List<Chain> items;
	
	public void put(Chain chain) {
		items.add(chain);
	}
	
	public List<Chain> get() {
		return items;
	}

}
