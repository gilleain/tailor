package tailor.experiment.description.group;

import tailor.experiment.description.GroupDescription;

public class GroupSequenceDescription {
	
	private GroupDescription start;
	
	private GroupDescription end;
	
	private int separation;

	public GroupSequenceDescription(GroupDescription start, GroupDescription end, int separation) {
		this.start = start;
		this.end = end;
		this.separation = separation;
	}

}
