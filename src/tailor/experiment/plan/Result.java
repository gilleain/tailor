package tailor.experiment.plan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
	
	public Result() {
		this.root = null;
	}
	
	public Result(Chain chain, Group group) {
		this.root = new Node(Level.CHAIN, chain);
		Node groupNode = new Node(Level.RESIDUE, group);
		this.root.children.add(groupNode);
		for (Atom atom : group.getAtoms()) {
			groupNode.children.add(new Node(Level.ATOM, atom));
		}
	}
	
	public Result(Chain chain, Group group, Atom atom) {
		// ... 
		this.root = new Node(Level.CHAIN, chain);
		Node groupNode = new Node(Level.RESIDUE, group);
		this.root.children.add(groupNode);
		groupNode.children.add(new Node(Level.ATOM, atom));
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
	
	public List<Group> getGroups() {
		// TODO Auto-generated method stub
		return null;
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
		boolean groupIdsEqual = gN.getResidueName().equals(gM.getResidueName()) 
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
			output.append(g.getResidueName()).append(g.getNumber()).append("/");
			addAtoms(child, output);
			if (numberOfChildren > 0 && counter < numberOfChildren - 1) {
				output.append("|");
			}
			counter++;
		}
		output.append(")");
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
	
	// TODO - simplify or ...
	public boolean hasSameGroup(Result result) {
		for (Node n : this.root.children) {
			Group gN = (Group) n.o;
			for (Node m : result.root.children) {
				Group gM = (Group) m.o;
				if (gN.getResidueName().equals(gM.getResidueName()) && gN.getNumber() == gM.getNumber()) {
					return true;
				}
			}
		}
		return false;
	}

	public void addAtom(Atom atom) {
		// TODO - always add to the first we find?
		Node groupNode = this.root.children.get(0);
		groupNode.children.add(new Node(Level.ATOM, atom));
	}

}
