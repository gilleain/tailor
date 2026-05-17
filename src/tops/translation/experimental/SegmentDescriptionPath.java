package tops.translation.experimental;

import tailor.description.ChainDescription;

public class SegmentDescriptionPath {
	
	private final ChainDescription chainDescription;
	
	private final SegmentDescription segmentDescription;

	public SegmentDescriptionPath(ChainDescription chainDescription, SegmentDescription segmentDescription) {
		this.chainDescription = chainDescription;
		this.segmentDescription = segmentDescription;
	}

	public ChainDescription getChainDescription() {
		return chainDescription;
	}

	public SegmentDescription getSegmentDescription() {
		return segmentDescription;
	}

}
