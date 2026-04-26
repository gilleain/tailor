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
	
	public static DescriptionPath getPath(ChainDescription chain, int residueNumber, String atomLabel) {
		GroupDescription group = chain.getGroupDescriptions().get(residueNumber);
		AtomDescription atom = group.getAtomDescription(atomLabel);
		return new DescriptionPath(group, atom);
	}
	
	public String toString() {
		String groupLabel = this.groupDescription.getLabel().orElse("*");
		return groupLabel + "/" + this.atomDescription;
	}
	
}
