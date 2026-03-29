package tailor.experiment.description;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroupDescription {
	
	private Optional<String> label;
	
	// Descriptions of the atoms to find
	private List<AtomDescription> atomDescriptions;
	
	// Descriptions of relationships to find between the atoms (in this group)
	private List<AtomDistanceDescription> atomSetDescriptions;	// TODO - make interface (and rename?)
	
	public GroupDescription() {
		this(null);
	}
	
	public GroupDescription(String label) {
		this.label = Optional.ofNullable(label);
		this.atomDescriptions = new ArrayList<>();
		this.atomSetDescriptions = new ArrayList<>();
	}
	
	public void addAtomDescription(AtomDescription atomDescription) {
		this.atomDescriptions.add(atomDescription);
	}
	
	public void addAtomSetDescription(AtomDistanceDescription atomSetDescription) {
		this.atomSetDescriptions.add(atomSetDescription);
	}
	
	public Optional<String> getLabel() {
		return label;
	}

	public List<AtomDescription> getAtomDescriptions() {
		return this.atomDescriptions;
	}

}
