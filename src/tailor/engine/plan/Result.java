package tailor.engine.plan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import tailor.api.Measurement;
import tailor.condition.AtomPartition;
import tailor.structure.Atom;
import tailor.structure.Chain;
import tailor.structure.Group;
import tailor.structure.Level;

/**
 * A result is a subtree of a structure that may be a part of a final result from a plan.
 */
public class Result {
	
	private class Node {
		public Level level;
		public Object o;
		public List<Node> children = new ArrayList<>();
		public Node(Level level, Object o) {
			this.level = level;
			this.o = o;
		}
	}
	
	private Node root;
	
	// store measurements
	private List<Measurement> measurements = new ArrayList<>();	// TODO
	
	private Result() {
		this.root = null;
	}
	
	public Result(Chain chain, Group... groups) {
		this(chain, Arrays.asList(groups));
	}
		
	public Result(Chain chain, List<Group> groups) {	
		this.root = new Node(Level.CHAIN, chain);
		for (Group group : groups) {
			Node groupNode = new Node(Level.RESIDUE, group);
			this.root.children.add(groupNode);
			// Note this makes a ref to _every_ atom in the group
			for (Atom atom : group.getAtoms()) {
				groupNode.children.add(new Node(Level.ATOM, atom));
			}
		}
	}
	
	public Result(Chain chain, Group group, Atom atom) {
		// ... 
		this.root = new Node(Level.CHAIN, chain);
		Node groupNode = new Node(Level.RESIDUE, group);
		this.root.children.add(groupNode);
		groupNode.children.add(new Node(Level.ATOM, atom));
	}
	
	public Result(Chain chain, Group group, List<Atom> atoms) {
		// ... 
		this.root = new Node(Level.CHAIN, chain);
		Node groupNode = new Node(Level.RESIDUE, group);
		this.root.children.add(groupNode);
		for (Atom atom : atoms) {
			groupNode.children.add(new Node(Level.ATOM, atom));
		}
	}
	
	public List<Atom> getAtoms() {
		// TODO - does this make sense?
		List<Atom> atoms = new ArrayList<>();
		for (Node groupNode : this.root.children) {
			for (Node atomNode : groupNode.children) {
				atoms.add((Atom)atomNode.o);
			}
		}
		return atoms;
	}
	
	public void addMeasurement(Measurement measurement) {
		this.measurements.add(measurement);	// TODO
	}
	

	public AtomPartition getAtomPartition() {
		List<List<Atom>> atomParts = new ArrayList<>();
		for (Node groupNode : this.root.children) {
			List<Atom> atoms = new ArrayList<>();
			for (Node atomNode : groupNode.children) {
				atoms.add((Atom)atomNode.o);
			}
			atomParts.add(atoms);
		}
		return new AtomPartition(atomParts);
	}
	
	public List<Group> getGroups() {
		List<Group> groups = new ArrayList<>();
		for (Node groupNode : this.root.children) {
			groups.add((Group) groupNode.o);
		}
		return groups;
	}

	public boolean equals(Result other) {
		for (Node groupNode : this.root.children) {
			if (noMatch(groupNode, other)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean noMatch(Node thisGroupNode, Result other) {
		// TODO - would not need this if nodes were ordered...
		for (Node otherGroupNode : other.root.children) {
			if (isMatch(thisGroupNode, otherGroupNode)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isMatch(Node thisGroupNode, Node otherGroupNode) {
		Group gN = (Group) thisGroupNode.o;
		Group gM = (Group) otherGroupNode.o;
		boolean groupIdsEqual = gN.getName().equals(gM.getName()) 
							  && gN.getResidueId().getResseq() == gM.getResidueId().getResseq();
		boolean atomsEqual = atomString(gN).equals(atomString(gM));
		return groupIdsEqual && atomsEqual;
	}
	
	private String atomString(Group g) {
		return g.getAtoms().stream().map(Atom::getName).collect(Collectors.joining());
	}

	public Result merge(Result anotherResult) {
		// merge anotherResult with this one
		for (Node child : anotherResult.root.children) {
			Node groupCopy = new Node(Level.RESIDUE, child.o);
			for (Node atom : child.children) {
				groupCopy.children.add(new Node(Level.ATOM, atom.o));
			}
			this.root.children.add(groupCopy);
			// TODO - use a data structure that maintains sort order?
			this.root.children.sort(groupComparator);
		}
		
		return this;
	}
	
	private Comparator<Node> groupComparator = new Comparator<Result.Node>() {
		
		@Override
		public int compare(Node n, Node m) {
			return ((Group)n.o).getNumber().compareTo(((Group)m.o).getNumber());
		}
	};
	
	public Result copy() {
		Result copy = new Result();
		copy.root = new Node(Level.CHAIN, this.root.o);
		for (Node child : root.children) {
			Node groupCopy = new Node(Level.RESIDUE, child.o);
			for (Node atom : child.children) {
				groupCopy.children.add(new Node(Level.ATOM, atom.o));
			}
			copy.root.children.add(groupCopy);	
		}
		
		return copy;
	}
	
	public Result copyWithoutAtoms() {
		Result copy = new Result();
		copy.root = new Node(Level.CHAIN, this.root.o);
		// merge anotherResult with this one
		for (Node child : root.children) {
			Node groupCopy = new Node(Level.RESIDUE, child.o);
			copy.root.children.add(groupCopy);	
		}
		
		return copy;
	}
	
	public String toString() {
		StringBuffer output = new StringBuffer();
		Chain chain = (Chain)this.root.o;
		output.append(chain.getName()).append("(");
		int counter = 0;
		int numberOfChildren = this.root.children.size();
		for (Node child : this.root.children) {
			Group g = (Group)child.o;
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
	
	private void addAtoms(Node group, StringBuffer output) {
		int counter = 0;
		int numberOfChildren = group.children.size();
		for (Node leaf : group.children) {
			Atom a = (Atom)leaf.o;
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
		for (Node n : this.root.children) {
			Group gN = (Group) n.o;
			for (Node m : other.root.children) {
				Group gM = (Group) m.o;
				if (gN.getNumber() >= gM.getNumber()) {
					return true;
				}
			}
		}
		return false;
	}
	
	// TODO - simplify or ...
	public boolean hasSameGroup(Result other) {
		for (Node n : this.root.children) {
			Group gN = (Group) n.o;
			for (Node m : other.root.children) {
				Group gM = (Group) m.o;
				if (gN.getName().equals(gM.getName()) && gN.getNumber() == gM.getNumber()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Group lastGroup() {
		return (Group)this.root.children.get(this.root.children.size() - 1).o;
	}
	

	public int separation(Result result) {
		return result.lastGroup().getNumber() - lastGroup().getNumber();
	}
	
	public void add(Group group, List<Atom> atoms) {
		Node groupNode = new Node(Level.RESIDUE, group);
		this.root.children.add(groupNode);
		for (Atom atom : atoms) {
			groupNode.children.add(new Node(Level.ATOM, atom));
		}
	}
	
	public void addAtomToGroup(Group group, Atom atom) {
		for (Node groupNode : this.root.children) {
			Group groupO = (Group)groupNode.o;
			if (groupO == group) {
				groupNode.children.add(new Node(Level.ATOM, atom));
			}
		}
	}

	public void addAtom(Atom atom) {
		// TODO - always add to the first we find?
		Node groupNode = this.root.children.get(0);
		groupNode.children.add(new Node(Level.ATOM, atom));
	}

	public List<Measurement> getMeasurements() {
		return this.measurements;
	}


}
