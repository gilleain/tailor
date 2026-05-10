package tailor.condition;

import java.util.List;

import tops.translation.model.BackboneSegment;

public class SegmentPartition {
	
	private final List<List<BackboneSegment>> parts;
	
	public SegmentPartition(List<List<BackboneSegment>> parts) {
		this.parts = parts;
	}
	
	public int size() {
		return parts.stream().map(List::size).reduce(0, Integer::sum);
	}
	
	public List<BackboneSegment> getPart(int index) {
		return this.parts.get(index);
	}
	
	public String toString() {
		return this.parts.stream().map(this::toString).toList().toString();
	}
	
	private String toString(List<BackboneSegment> segments) {
		return segments.stream().map(BackboneSegment::toCompactString).toList().toString();
	}
}
