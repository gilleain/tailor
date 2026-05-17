package tops.translation.experimental;

import java.util.logging.Logger;

import tailor.engine.operator.AbstractOperator;
import tailor.engine.operator.Pipe;
import tailor.engine.plan.Result;
import tailor.structure.Segment;

public class SegmentTypeFilter extends AbstractOperator {
	
	private static Logger logger = Logger.getLogger(SegmentTypeFilter.class.getName());
	
	private final Segment.Type segmentType;
	
	private final Pipe input;
	
	public SegmentTypeFilter(Segment.Type segmentType, Pipe input) {
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
			for (Segment segment : result.getSegments()) {
				if (segmentType == segment.getType()) { 
//					logger.info("Accepted " + segment);
					getOutput().put(result.copy());
				} else {
//					logger.info("Rejected " + segment);
				}
			}
		}
	}

	@Override
	public String description() {
		return "Segment type";
	}
}
