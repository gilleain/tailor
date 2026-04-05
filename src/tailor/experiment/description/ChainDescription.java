package tailor.experiment.description;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChainDescription {
	
	private Optional<String> label;
	
	// Descriptions of the groups to find
	private List<GroupDescription> groupDescriptions;
	
	// Descriptions of relationships to find between the atoms across groups
	private List<AtomListDescription> atomListDescriptions;

	public ChainDescription() {
		this(null);
	}
	
	public ChainDescription(String label) {
		this.label = Optional.ofNullable(label);
		this.groupDescriptions = new ArrayList<>();
		this.atomListDescriptions = new ArrayList<>();
	}
	
	public void addGroupDescription(GroupDescription groupDescription) {
		this.groupDescriptions.add(groupDescription);
	}
	
	public void addAtomSetDescription(AtomListDescription atomSetDescription) {
		this.atomListDescriptions.add(atomSetDescription);
	}
	
	public Optional<String> getLabel() {
		return label;
	}

	public List<GroupDescription> getGroupDescriptions() {
		return this.groupDescriptions;
	}

	public List<AtomListDescription> getAtomListDescriptions() {
		return atomListDescriptions;
	}
 
}
