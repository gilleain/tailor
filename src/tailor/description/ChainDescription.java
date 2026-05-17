package tailor.description;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import tailor.api.AtomListDescription;
import tailor.api.AtomListMeasure;
import tailor.api.SegmentListDescription;
import tailor.description.group.GroupSequenceDescription;
import tailor.description.segment.SegmentDescription;

public class ChainDescription {
	
	private int index;	// index of description in the list
	
	private Optional<String> label;
	
	// Descriptions of the segments to find
	private List<SegmentDescription> segments;
	
	// Descriptions of relationships to find between the segments
	private List<SegmentListDescription> segmentListDescriptions;
	
	// Descriptions of the groups to find
	private List<GroupDescription> groupDescriptions;
	
	// Descriptions of relationships to find between the atoms across groups
	private List<AtomListDescription> atomListDescriptions;
	
	// Descriptions of sequence relationships between the groups
	private List<GroupSequenceDescription> groupSequenceDescriptions;
	
	// Measurements to make between atoms across groups
	private List<AtomListMeasure> atomListMeasures;

	public ChainDescription() {
		this(null);
	}
	
	public ChainDescription(String label) {
		this.label = Optional.ofNullable(label);
		this.segments = new ArrayList<>();
		this.segmentListDescriptions = new ArrayList<>();
		this.groupDescriptions = new ArrayList<>();
		this.atomListDescriptions = new ArrayList<>();
		this.groupSequenceDescriptions = new ArrayList<>();
		this.atomListMeasures = new ArrayList<>();
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public Optional<String> getLabel() {
		return label;
	}
	
	public List<SegmentDescription> getSegments() {
		return segments;
	}

	public void addSegment(SegmentDescription segmentDescription) {
		this.segments.add(segmentDescription);
	}
	
	public List<SegmentListDescription> getSegmentListDescription() {
		return segmentListDescriptions;
	}

	public void addSegmentListDescriptions(SegmentListDescription... segmentListDescription) {
		this.segmentListDescriptions.addAll(Arrays.asList(segmentListDescription));
	}

	public void addGroupDescription(GroupDescription groupDescription) {
		groupDescription.setIndex(this.groupDescriptions.size());
		this.groupDescriptions.add(groupDescription);
	}

	public void addGroupDescriptions(GroupDescription... groups) {
		for (GroupDescription group : groups) {
			addGroupDescription(group);
		}
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
	
	public void addAtomListMeasures(AtomListMeasure... atomListMeasure) {
		this.atomListMeasures.addAll(Arrays.asList(atomListMeasure));
	}
	
	public List<AtomListMeasure> getAtomListMeasures() {
		return this.atomListMeasures;
	}
 
}
