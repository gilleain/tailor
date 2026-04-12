package tailor.experiment.description.group;

import tailor.experiment.description.GroupDescription;

public class GroupSequenceDescription {
	
	private final GroupDescription start;
	
	private final GroupDescription end;
	
	private final int separation;

	public GroupSequenceDescription(GroupDescription start, GroupDescription end, int separation) {
		this.start = start;
		this.end = end;
		this.separation = separation;
	}

	public int getSeparation() {
		return separation;
	}

	public GroupDescription getStart() {
		return start;
	}

	public GroupDescription getEnd() {
		return end;
	}
}