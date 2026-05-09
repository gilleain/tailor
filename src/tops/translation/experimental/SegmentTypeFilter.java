package tops.translation.experimental;

import java.util.logging.Logger;

import tops.translation.model.BackboneSegment;

public class SegmentTypeFilter extends AbstractOperator {
	
	private static Logger logger = Logger.getLogger(SegmentTypeFilter.class.getName());
	
	private final BackboneSegment.Type segmentType;
	
	private final Pipe input;
	
	public SegmentTypeFilter(BackboneSegment.Type segmentType, Pipe input) {
		this.segmentType = segmentType;
		this.input = input;
	}
	
	public void setId(String id) {
		super.setId(id);
		this.input.registerSink(this);
	}

	@Override
	public void run() {
		while (input.hasNext()) {
			Result result = input.getNext();
//			logger.info("Filtering " + result);
			for (BackboneSegment segment : result.getSegments()) {
				if (segmentType == segment.getType()) { 
//					logger.info("Accepted " + segment);
					getOutput().put(result.copy());
				} else {
//					logger.info("Rejected " + segment);
				}
			}
		}
	}
}
