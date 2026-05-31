package tailor.partition;

import java.util.List;

import tailor.structure.Group;

/**
 *  Partition of groups by the chains they are part of - like a 'slice' across the result tree.
 */
public class GroupPartition {
	
	private final List<List<Group>> parts;
	
	public GroupPartition(List<List<Group>> parts) {
		this.parts = parts;
	}
	
	public int size() {
		return parts.stream().map(List::size).reduce(0, Integer::sum);
	}
	
	public List<Group> getPart(int index) {
		return this.parts.get(index);
	}
	
	public String toString() {
		return this.parts.stream().map(this::toString).toList().toString();
	}
	
	private String toString(List<Group> atoms) {
		return atoms.stream().map(Group::getName).toList().toString();
	}
}
