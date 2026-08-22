package tailor.engine.physical;

import tailor.structure.Chain;
import tailor.structure.Group;

public class GroupFilter implements PhysicalOperator {
	
	private String groupType;

	private ChainPipe input;
	
	private GroupPipe output;
	
	public GroupFilter(String groupType) {
		this.groupType = groupType;
	}

	public void run() {
		for (Chain chain : input.get()) {
			for (Group group : chain.getGroups()) {
				if (group.getType().equals(groupType)) {
					output.put(group);
				}
			}
		}
	}

}
