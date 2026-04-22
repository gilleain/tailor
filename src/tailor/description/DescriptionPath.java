package tailor.description;

public class DescriptionPath {
	
	private GroupDescription groupDescription;
	
	private AtomDescription atomDescription;

	public DescriptionPath(GroupDescription groupDescription, AtomDescription atomDescription) {
		this.groupDescription = groupDescription;
		this.atomDescription = atomDescription;
	}

	public GroupDescription getGroupDescription() {
		return groupDescription;
	}

	public AtomDescription getAtomDescription() {
		return atomDescription;
	}
	
}
