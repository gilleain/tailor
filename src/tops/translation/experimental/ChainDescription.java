package tops.translation.experimental;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tailor.api.SegmentListDescription;

public class ChainDescription {
	
	private int index;	// index of description in the list
	
	private List<SegmentDescription> segments;
	
	private List<SegmentListDescription> segmentListDescriptions;
	
	public ChainDescription() {
		this.segments = new ArrayList<>();
		this.segmentListDescriptions = new ArrayList<>();
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
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

	public void addSegmentListDescriptions(SegmentListDescription... segmentListDescription) {
		this.segmentListDescriptions.addAll(Arrays.asList(segmentListDescription));
	}

}
