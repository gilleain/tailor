package tailor.engine.operator;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import tailor.api.SegmentListDescription;
import tailor.condition.SegmentPartition;
import tailor.engine.plan.Result;

public class FilterSegmentByListDescription extends AbstractOperator {
	
	private Logger logger = Logger.getLogger(FilterSegmentByListDescription.class.getName());
	
	private List<SegmentListDescription> listDescriptions;
	
	private Pipe input;
	
	public FilterSegmentByListDescription(Pipe input, SegmentListDescription... listDescriptions) {
		this(input, Arrays.asList(listDescriptions));
	}
	
	public FilterSegmentByListDescription(Pipe input, List<SegmentListDescription> listDescriptions) {
		this.input = input;
		this.listDescriptions = listDescriptions;
	}

	@Override
	public void run() {
		int filterInCount = 0;
		int filterOutCount = 0;
		while (input.hasNext()) {
			Result result = input.getNext();
			SegmentPartition segmentPartition = result.getSegmentPartition();
			if (isAccepted(segmentPartition)) {
				getOutput().put(result);
				filterInCount++;
			} else {
				filterOutCount++;
			}
		}
		logger.info(description() + " filtered: IN " + filterInCount + " OUT " + filterOutCount);
	}
	
	public String description() {
		return "FilterSegments";	// TODO
	}
	
	private boolean isAccepted(SegmentPartition segmentPartition) {
		for (SegmentListDescription segmentListDescription : listDescriptions) {
			if (!segmentListDescription.apply(segmentPartition)) {
				return false;
			}
		}
		return true;
	}

}
