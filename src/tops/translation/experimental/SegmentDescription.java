package tops.translation.experimental;

import tops.translation.model.BackboneSegment;

public class SegmentDescription {
	
	private final BackboneSegment.Type type;
	
	public SegmentDescription(BackboneSegment.Type type) {
		this.type = type;
	}

	public BackboneSegment.Type getType() {
		return this.type;
	}

}
