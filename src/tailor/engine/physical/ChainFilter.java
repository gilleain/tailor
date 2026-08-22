package tailor.engine.physical;

import java.util.List;

import tailor.structure.Chain;
import tailor.structure.PolymerType;

public class ChainFilter implements PhysicalOperator {
	
	private PolymerType type;	// TODO - convert to condition
	
	private List<Chain> chains; // TODO - input pipe
	
	private ChainPipe output;
	
	public ChainFilter(PolymerType type) {
		this.type = type;
	}
	
	public void run() {
		for (Chain chain : chains) {
			if (chain.getType() == type) {
				output.put(chain);
			}
		}
	}

}
