package tops.translation.experimental;

import java.util.ArrayList;
import java.util.List;

import tailor.api.SegmentPropertyDescription;
import tops.translation.model.Segment;

public class SegmentDescription {
	
	private final Segment.Type type;
	
	private final List<SegmentPropertyDescription> segmentPropertyDescriptions;
	
	public SegmentDescription(Segment.Type type) {
		this.type = type;
		this.segmentPropertyDescriptions = new ArrayList<>();
	}

	public Segment.Type getType() {
		return this.type;
	}

	public void addPropertyDescription(SegmentPropertyDescription segmentPropertyDescription) {
		this.segmentPropertyDescriptions.add(segmentPropertyDescription);
	}

	public List<SegmentPropertyDescription> getPropertyDescriptions() {
		return this.segmentPropertyDescriptions;
	}

}
