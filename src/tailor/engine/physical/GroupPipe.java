package tailor.engine.physical;

import java.util.List;

import tailor.structure.Group;

public class GroupPipe {

	private List<Group> items;
	
	public void put(Group chain) {
		items.add(chain);
	}
	
	public List<Group> get() {
		return items;
	}
}
