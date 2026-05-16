package tailor.engine.plan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import tailor.api.Measurement;
import tailor.condition.AtomPartition;
import tailor.structure.Chain;
import tailor.structure.Group;
import tops.translation.model.Atom;

/**
 * A result is a subtree of a structure that may be a part of a final result from a plan.
 */
public class Result {
	
	private class CRef {
		public Chain chain;
		public List<GRef> groupRefs = new ArrayList<>();
//		public List<SRef> segmentRefs = new ArrayList<>(); // TODO
		public CRef(Chain chain) {
			this.chain = chain;
		}
	}
	
//	private class SRef {	// TODO
//		public BackboneSegment segment;
//		public List<RRef> residueRefs = new ArrayList<>();
//		public SRef(BackboneSegment segment) {
//			this.segment = segment;
//		}
//	}
	
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
	
	public Result(Chain chain, Group... groups) {
		this(chain, Arrays.asList(groups));
	}
		
	public Result(Chain chain, List<Group> groups) {	
		this.root = new CRef(chain);
		for (Group group : groups) {
			GRef groupNode = new GRef(group);
			this.root.groupRefs.add(groupNode);
			// Note this makes a ref to _every_ atom in the group
			for (Atom atom : group.getAtoms()) {
				groupNode.atomRefs.add(new ARef(atom));
			}
		}
	}
	
	public Result(Chain chain, Group group, Atom atom) {
		// ... 
		this.root = new CRef(chain);
		GRef groupNode = new GRef(group);
		this.root.groupRefs.add(groupNode);
		groupNode.atomRefs.add(new ARef(atom));
	}
	
	public Result(Chain chain, Group group, List<Atom> atoms) {
		// ... 
		this.root = new CRef(chain);
		GRef groupNode = new GRef(group);
		this.root.groupRefs.add(groupNode);
		for (Atom atom : atoms) {
			groupNode.atomRefs.add(new ARef(atom));
		}
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

	public Result merge(Result anotherResult) {
		// merge anotherResult with this one
		for (GRef groupRef : anotherResult.root.groupRefs) {
			GRef groupCopy = new GRef(groupRef.group);
			for (ARef atomRef : groupRef.atomRefs) {
				groupCopy.atomRefs.add(new ARef(atomRef.atom));
			}
			this.root.groupRefs.add(groupCopy);
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
		for (GRef child : root.groupRefs) {
			GRef groupCopy = new GRef(child.group);
			for (ARef atomRef : child.atomRefs) {
				groupCopy.atomRefs.add(new ARef(atomRef.atom));
			}
			copy.root.groupRefs.add(groupCopy);
		}
		
		return copy;
	}
	
	public Result copyWithoutAtoms() {
		Result copy = new Result();
		copy.root = new CRef(this.root.chain);
		// merge anotherResult with this one
		for (GRef child : root.groupRefs) {
			GRef groupCopy = new GRef(child.group);
			copy.root.groupRefs.add(groupCopy);	
		}
		
		return copy;
	}
	
	public String toString() {
		StringBuffer output = new StringBuffer();
		output.append(this.root.chain.getName()).append("(");
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
	public boolean greaterThanOrEqual(Result other) {
		// TODO combine with the hasSameGroup
		// or .. do we even need to as same group would be equal?
		for (GRef n : this.root.groupRefs) {
			for (GRef m : other.root.groupRefs) {
				if (n.group.getNumber() >= m.group.getNumber()) {
					return true;
				}
			}
		}
		return false;
	}
	
	// TODO - simplify or ...
	public boolean hasSameGroup(Result other) {
		for (GRef n : this.root.groupRefs) {
			for (GRef m : other.root.groupRefs) {
				if (n.group.getName().equals(m.group.getName()) 
						&& n.group.getNumber() == n.group.getNumber()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Group lastGroup() {
		return this.root.groupRefs.get(this.root.groupRefs.size() - 1).group;
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
