package tailor.condition;

import java.util.List;

import tailor.structure.Atom;

public class AtomPartition {
	
	private final List<List<Atom>> parts;
	
	public AtomPartition(List<List<Atom>> parts) {
		this.parts = parts;
	}
	
	public int size() {
		return parts.stream().map(List::size).reduce(0, Integer::sum);
	}
	
	public List<Atom> getPart(int index) {
		return this.parts.get(index);
	}
	
	public String toString() {
		return this.parts.stream().map(this::toString).toList().toString();
	}
	
	private String toString(List<Atom> atoms) {
		return atoms.stream().map(Atom::getName).toList().toString();
	}
}
