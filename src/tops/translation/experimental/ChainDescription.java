package tops.translation.experimental;

import java.util.ArrayList;
import java.util.List;

public class ChainDescription {
	
	private List<SegmentDescription> segments;
	
	public ChainDescription() {
		this.segments = new ArrayList<>();
	}
	
	public List<SegmentDescription> getSegments() {
		return segments;
	}

	public void addSegment(SegmentDescription segmentDescription) {
		this.segments.add(segmentDescription);
	}

}
