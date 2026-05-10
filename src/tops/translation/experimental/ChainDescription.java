package tops.translation.experimental;

import java.util.ArrayList;
import java.util.List;

import tailor.api.SegmentListDescription;

public class ChainDescription {
	
	private List<SegmentDescription> segments;
	
	private List<SegmentListDescription> segmentListDescriptions;
	
	public ChainDescription() {
		this.segments = new ArrayList<>();
		this.segmentListDescriptions = new ArrayList<>();
	}
	
	public List<SegmentDescription> getSegments() {
		return segments;
	}

	public void addSegment(SegmentDescription segmentDescription) {
		this.segments.add(segmentDescription);
	}
	
	public List<SegmentListDescription> getSegmentListDescription() {
		return segmentListDescriptions;
	}

	public void addSegmentListDescription(SegmentListDescription segmentListDescription) {
		this.segmentListDescriptions.add(segmentListDescription);
	}

}
