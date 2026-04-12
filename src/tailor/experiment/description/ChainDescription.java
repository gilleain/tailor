package tailor.experiment.description;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import tailor.experiment.api.AtomListDescription;
import tailor.experiment.description.group.GroupSequenceDescription;

public class ChainDescription {
	
	private Optional<String> label;
	
	// Descriptions of the groups to find
	private List<GroupDescription> groupDescriptions;
	
	// Descriptions of relationships to find between the atoms across groups
	private List<AtomListDescription> atomListDescriptions;
	
	// Descriptions of sequence relationships between the groups
	private List<GroupSequenceDescription> groupSequenceDescriptions;

	public ChainDescription() {
		this(null);
	}
	
	public ChainDescription(String label) {
		this.label = Optional.ofNullable(label);
		this.groupDescriptions = new ArrayList<>();
		this.atomListDescriptions = new ArrayList<>();
		this.groupSequenceDescriptions = new ArrayList<>();
	}
	
	public Optional<String> getLabel() {
		return label;
	}

	public void addGroupDescription(GroupDescription groupDescription) {
		this.groupDescriptions.add(groupDescription);
	}

	public void addGroupDescriptions(GroupDescription... groups) {
		this.groupDescriptions.addAll(Arrays.asList(groups));
	}

	public List<GroupDescription> getGroupDescriptions() {
		return this.groupDescriptions;
	}

	public List<AtomListDescription> getAtomListDescriptions() {
		return atomListDescriptions;
	}

	public void addAtomListDescriptions(AtomListDescription... atomListDescription) {
		this.atomListDescriptions.addAll(Arrays.asList(atomListDescription));
	}
	
	public void addGroupSequenceDescriptions(GroupSequenceDescription... groupSequenceDescription) {
		this.groupSequenceDescriptions.addAll(Arrays.asList(groupSequenceDescription));
	}
	
	public List<GroupSequenceDescription> getGroupSequenceDescriptions() {
		return this.groupSequenceDescriptions;
	}
 
}
