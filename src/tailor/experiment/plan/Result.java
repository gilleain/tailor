package tailor.experiment.plan;

import java.util.ArrayList;
import java.util.List;

import tailor.structure.Atom;
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
	
	public Result(Group group, Atom atom) { // TODO .. chain
		// ... 
		this.root = new Node(Level.CHAIN, null);	// TODO
		Node groupNode = new Node(Level.RESIDUE, group);
		this.root.children.add(groupNode);
		groupNode.children.add(new Node(Level.ATOM, atom));
	}
	
	public Atom getAtom() {
		// TODO
		return null;
	}

	public Result merge(Result anotherResult) {
		// merge anotherResult with this one
		for (Node child : anotherResult.root.children) {
			Node groupCopy = new Node(Level.RESIDUE, child.o);
			for (Node atom : child.children) {
				groupCopy.children.add(new Node(Level.ATOM, atom.o));
			}
			this.root.children.add(groupCopy);	
		}
		
		return this;
	}
	
	public Result copy() {
		Result copy = new Result();
		copy.root = new Node(Level.CHAIN, null);	// TODO .. chain
		// merge anotherResult with this one
		for (Node child : root.children) {
			Node groupCopy = new Node(Level.RESIDUE, child.o);
			for (Node atom : child.children) {
				groupCopy.children.add(new Node(Level.ATOM, atom.o));
			}
			copy.root.children.add(groupCopy);	
		}
		
		return copy;
	}
	
	public String toString() {
		StringBuffer output = new StringBuffer();
		output.append(this.root.o).append("(");
		int counter = 0;
		int numberOfChildren = this.root.children.size();
		for (Node child : this.root.children) {
			Group g = (Group)child.o;
			output.append(g.getResidueName()).append(g.getResidueId().getResseq()).append("/");
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
				if (gN.getResidueName().equals(gM.getResidueName()) 
						&& gN.getResidueId().getResseq() == gM.getResidueId().getResseq()) {
					return true;
				}
			}
		}
		return false;
	}

}
