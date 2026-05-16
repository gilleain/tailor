package tailor.condition;

import java.util.List;

import tailor.structure.Segment;

public class SegmentPartition {
	
	private final List<List<Segment>> parts;
	
	public SegmentPartition(List<List<Segment>> parts) {
		this.parts = parts;
	}
	
	public int size() {
		return parts.stream().map(List::size).reduce(0, Integer::sum);
	}
	
	public List<Segment> getPart(int index) {
		return this.parts.get(index);
	}
	
	public String toString() {
		return this.parts.stream().map(this::toString).toList().toString();
	}
	
	private String toString(List<Segment> segments) {
		return segments.stream().map(Segment::toCompactString).toList().toString();
	}
}
