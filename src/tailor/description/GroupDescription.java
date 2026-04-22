package tailor.description;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import tailor.api.AtomListDescription;

public class GroupDescription {
	
	private Optional<String> label;
	
	// Descriptions of the atoms to find
	private List<AtomDescription> atomDescriptions;
	
	// Descriptions of relationships to find between the atoms (in this group)
	private List<AtomListDescription> atomListDescriptions;
	
	public GroupDescription() {
		this(null);
	}
	
	public GroupDescription(String label) {
		this.label = Optional.ofNullable(label);
		this.atomDescriptions = new ArrayList<>();
		this.atomListDescriptions = new ArrayList<>();
	}
	
	public void addAtomDescription(AtomDescription atomDescription) {
		this.atomDescriptions.add(atomDescription);
	}
	
	public void addAtomListDescriptions(AtomListDescription... atomListDescription) {
		this.atomListDescriptions.addAll(Arrays.asList(atomListDescription));
	}
	
	public List<AtomListDescription> getAtomListDescriptions() {
		return this.atomListDescriptions;
	}
	
	public Optional<String> getLabel() {
		return label;
	}

	public AtomDescription getAtomDescription(String atomLabel) {
		return atomDescriptions.stream().filter(a -> a.getLabel().equals(atomLabel)).findFirst().orElseThrow();
	}

	public List<AtomDescription> getAtomDescriptions() {
		return this.atomDescriptions;
	}
	
	public String toString() {
		return (label.isEmpty()? "*" : label.get()) + "("
			+ this.atomDescriptions.stream().map(AtomDescription::getLabel).collect(Collectors.joining(","))
			+ ")";
	}
	
	private int index;	// TODO - currently need this to preserve creation order - could be a better way...

	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return this.index;
	}

}
