package tailor.engine.plan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import tailor.api.Measurement;
import tailor.condition.AtomPartition;
import tailor.condition.SegmentPartition;
import tailor.structure.Atom;
import tailor.structure.Chain;
import tailor.structure.Group;
import tailor.structure.Segment;

/**
 * A result is a subtree of a structure that may be a part of a final result from a plan.
 */
public class Result {
	
	private class CRef {
		public Chain chain;
		public List<GRef> groupRefs = new ArrayList<>();
		public List<SRef> segmentRefs = new ArrayList<>(); 
		public CRef(Chain chain) {
			this.chain = chain;
		}
	}
	
	private class SRef {
		public Segment segment;
		public List<GRef> groupRefs = new ArrayList<>();
		public SRef(Segment segment) {
			this.segment = segment;
		}
		public Segment getSegment() {
			return segment;
		}
	}
	
	private class GRef {
		public Group group;
		public List<ARef> atomRefs = new ArrayList<>();
		public GRef(Group group) {
			this.group = group;
		}
		public Group getGroup() {
			return this.group;
		}
	}
	
	private class ARef {	// TODO - could consider removing this ...
		public Atom atom;
		public ARef(Atom atom) {
			this.atom = atom;
		}
	}
	
	private CRef root;
	
	// store measurements
	private List<Measurement<?>> measurements = new ArrayList<>();	// TODO
	
	private Result() {
		this.root = null;
	}
	
	public Result(Chain chain) {
		this.root = new CRef(chain);
	}
	
	public Result(Chain chain, Group... groups) {
		this(chain, Arrays.asList(groups));
	}
	
	public Result(Chain chain, Segment... segments) {
		this(chain);
		for (Segment segment : segments) {
			SRef segmentRef = new SRef(segment);
			this.root.segmentRefs.add(segmentRef);
			for (Group group : segment.getGroups()) {
				segmentRef.groupRefs.add(toGRef(group));
			}
		}
	}
		
	public Result(Chain chain, List<Group> groups) {	
		this(chain);
		for (Group group : groups) {
			// Note this makes a ref to _every_ atom in the group
			this.root.groupRefs.add(addAtoms(new GRef(group), group.getAtoms()));
		}
	}
	
	public Result(Chain chain, Group group, Atom atom) {
		this.root = new CRef(chain);
		GRef groupNode = new GRef(group);
		this.root.groupRefs.add(groupNode);
		groupNode.atomRefs.add(new ARef(atom));
	}
	
	public Result(Chain chain, Group group, List<Atom> atoms) {
		this.root = new CRef(chain);
		this.root.groupRefs.add(toGRef(group));
	}
	
	private GRef toGRef(Group group) {
		return addAtoms(new GRef(group), group.getAtoms());
	}
	
	private GRef addAtoms(GRef groupNode, List<Atom> atoms) {
		for (Atom atom : atoms) {
			groupNode.atomRefs.add(new ARef(atom));
		}
		return groupNode;
	}
	
	public List<Atom> getAtoms() {
		// TODO - does this make sense?
		List<Atom> atoms = new ArrayList<>();
		for (GRef groupNode : this.root.groupRefs) {
			for (ARef atomNode : groupNode.atomRefs) {
				atoms.add(atomNode.atom);
			}
		}
		return atoms;
	}
	
	public void addMeasurement(Measurement<?> measurement) {
		this.measurements.add(measurement);	// TODO
	}
	

	public AtomPartition getAtomPartition() {
		List<List<Atom>> atomParts = new ArrayList<>();
		for (GRef groupNode : this.root.groupRefs) {
			List<Atom> atoms = new ArrayList<>();
			for (ARef atomNode : groupNode.atomRefs) {
				atoms.add(atomNode.atom);
			}
			atomParts.add(atoms);
		}
		return new AtomPartition(atomParts);
	}
	
	public List<Group> getGroups() {
		return this.root.groupRefs.stream().map(GRef::getGroup).toList();
	}
	
	public List<Segment> getSegments() {
		List<Segment> segments = new ArrayList<>();
		for (SRef segmentRef : this.root.segmentRefs) {
			segments.add(segmentRef.segment);
		}
		return segments;
	}

	public SegmentPartition getSegmentPartition() {
		List<List<Segment>> parts = new ArrayList<>();
		parts.add(getSegments(root));
		return new SegmentPartition(parts);
	}
	
	private List<Segment> getSegments(CRef cRef) {
		return cRef.segmentRefs.stream().map(SRef::getSegment).toList();
	}

	public boolean equals(Result other) {
		for (GRef groupNode : this.root.groupRefs) {
			if (noMatch(groupNode, other)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean noMatch(GRef thisGroupNode, Result other) {
		// TODO - would not need this if nodes were ordered...
		for (GRef otherGroupNode : other.root.groupRefs) {
			if (isMatch(thisGroupNode, otherGroupNode)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isMatch(GRef thisGroupNode, GRef otherGroupNode) {
		Group gN = thisGroupNode.group;
		Group gM = otherGroupNode.group;
		boolean groupIdsEqual = gN.getName().equals(gM.getName()) 
							  && gN.getNumber() == gM.getNumber();
		boolean atomsEqual = atomString(gN).equals(atomString(gM));
		return groupIdsEqual && atomsEqual;
	}
	
	private String atomString(Group g) {
		return g.getAtoms().stream().map(Atom::getName).collect(Collectors.joining());
	}

	public Result merge(Result other) {
		// merge other with this one
		for (SRef otherS : other.root.segmentRefs) {
			SRef sCopy = new SRef(otherS.segment);
			this.root.segmentRefs.add(sCopy);
			for (GRef groupRef : otherS.groupRefs) {
				sCopy.groupRefs.add(groupCopy(groupRef));
				sCopy.groupRefs.sort(groupComparator);
			}
		}
		for (GRef groupRef : other.root.groupRefs) {
			this.root.groupRefs.add(groupCopy(groupRef));
			// TODO - use a data structure that maintains sort order?
			this.root.groupRefs.sort(groupComparator);
		}
		
		return this;
	}
	
	private Comparator<GRef> groupComparator = new Comparator<Result.GRef>() {
		
		@Override
		public int compare(GRef n, GRef m) {
			return n.group.getNumber().compareTo(m.group.getNumber());
		}
	};
	
	public Result copy() {
		Result copy = new Result();
		copy.root = new CRef(this.root.chain);
		for (SRef sRef : this.root.segmentRefs) {
			SRef sCopy = new SRef(sRef.segment);
			copy.root.segmentRefs.add(sCopy);
			for (GRef gRef : sRef.groupRefs) {
				sCopy.groupRefs.add(groupCopy(gRef));
			}
		}
		
		for (GRef gRef : root.groupRefs) {
			copy.root.groupRefs.add(groupCopy(gRef));
		}
		
		return copy;
	}
	
	private GRef groupCopy(GRef other) {
		GRef groupCopy = new GRef(other.group);
		for (ARef atomRef : other.atomRefs) {
			groupCopy.atomRefs.add(new ARef(atomRef.atom));
		}
		return groupCopy;
	}
	
	public Result copyWithoutAtoms() {
		Result copy = new Result();
		copy.root = new CRef(this.root.chain);
		for (SRef sRef : this.root.segmentRefs) {
			SRef sCopy = new SRef(sRef.segment);
			copy.root.segmentRefs.add(sCopy);
			for (GRef gRef : sRef.groupRefs) {
				sCopy.groupRefs.add(new GRef(gRef.group));
			}
		}
		for (GRef gRef : root.groupRefs) {
			copy.root.groupRefs.add(new GRef(gRef.group));	
		}
		
		return copy;
	}
	
	public String toString() {
		StringBuffer output = new StringBuffer();
		output.append(this.root.chain.getName()).append("(");
		String segments = this.root.segmentRefs.stream().map(s -> s.segment.toCompactString()).collect(Collectors.joining("|"));
		output.append("/").append(segments);
		int counter = 0;
		int numberOfChildren = this.root.groupRefs.size();
		for (GRef child : this.root.groupRefs) {
			Group g = child.group;
			output.append(g.getName()).append(g.getNumber()).append("/");
			addAtoms(child, output);
			if (numberOfChildren > 0 && counter < numberOfChildren - 1) {
				output.append("|");
			}
			counter++;
		}
		output.append(")");
		
		if (!measurements.isEmpty()) {
			output.append("\t");
			output.append(measurements.stream()
					.map(Measurement::toString)
					.collect(Collectors.joining("\t")));
		}
		
		return output.toString();
	}
	
	private void addAtoms(GRef group, StringBuffer output) {
		int counter = 0;
		int numberOfChildren = group.atomRefs.size();
		for (ARef leaf : group.atomRefs) {
			Atom a = leaf.atom;
			if (counter == numberOfChildren - 1) {
				output.append(a.getName());
			} else {
				output.append(a.getName()).append(",");
			}
			counter++;
		}
	}
	
	// Check the ordering of this Result compared to that
	public boolean lessThanAbs(Result other) {
		int thisMax = Math.max(getLastSegmentEnd(), getLastGroupIndex());
		int otherMin = Math.min(other.getFirstSegmentStart(), other.getFirstGroupIndex());
		return thisMax < otherMin;
	}
	
	public boolean lessThan(Result other) {
		int thisMin = Math.min(getFirstSegmentStart(), getFirstGroupIndex());
		int otherMin = Math.min(other.getFirstSegmentStart(), other.getFirstGroupIndex());
		return thisMin < otherMin;
	}
	
	private int getFirstSegmentStart() {
		if (this.root.segmentRefs.isEmpty()) {
			return Integer.MAX_VALUE;
		} else {
			Segment firstSegment = this.root.segmentRefs.get(0).segment;
			return firstSegment.firstResidue().getAbsoluteNumber();
		}
	}
	
	private int getFirstGroupIndex() {
		if (this.root.groupRefs.isEmpty()) {
			return Integer.MAX_VALUE;
		} else {
			Group firstGroup = this.root.groupRefs.get(0).group;
			return firstGroup.getAbsoluteNumber();
		}
	}
	
	private int getLastSegmentEnd() {
		if (this.root.segmentRefs.isEmpty()) {
			return 0;	// arbitrary low value
		} else {
			return lastSegment().lastResidue().getAbsoluteNumber();
		}
	}
	
	private int getLastGroupIndex() {
		if (this.root.groupRefs.isEmpty()) {
			return 0; 	// arbitrary low value
		} else {
			return lastGroup().getAbsoluteNumber();
		}
	}
	
	public Group lastGroup() {
		return this.root.groupRefs.get(this.root.groupRefs.size() - 1).group;
	}
	
	public Segment lastSegment() {
		int lastIndex = this.root.segmentRefs.size() - 1;
		return this.root.segmentRefs.get(lastIndex).segment;
	}

	public int separation(Result result) {
		return result.lastGroup().getNumber() - lastGroup().getNumber();
	}
	
	public void add(Group group, List<Atom> atoms) {
		GRef groupNode = new GRef(group);
		this.root.groupRefs.add(groupNode);
		for (Atom atom : atoms) {
			groupNode.atomRefs.add(new ARef(atom));
		}
	}
	
	public void addAtomToGroup(Group group, Atom atom) {
		for (GRef groupNode : this.root.groupRefs) {
			Group groupO = groupNode.group;
			if (groupO == group) {
				groupNode.atomRefs.add(new ARef(atom));
			}
		}
	}

	public void addAtom(Atom atom) {
		// TODO - always add to the first we find?
		GRef groupNode = this.root.groupRefs.get(0);
		groupNode.atomRefs.add(new ARef(atom));
	}

	public List<Measurement<?>> getMeasurements() {
		return this.measurements;
	}

}
