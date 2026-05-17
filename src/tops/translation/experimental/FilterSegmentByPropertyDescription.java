package tops.translation.experimental;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import tailor.api.SegmentPropertyDescription;
import tailor.engine.operator.AbstractOperator;
import tailor.engine.operator.Pipe;
import tailor.engine.plan.Result;
import tailor.structure.Segment;

public class FilterSegmentByPropertyDescription extends AbstractOperator {
	
	private Logger logger = Logger.getLogger(FilterSegmentByPropertyDescription.class.getName());
	
	private List<SegmentPropertyDescription> propertyDescriptions;
	
	private Pipe input;
	
	public FilterSegmentByPropertyDescription(Pipe input, SegmentPropertyDescription... propertyDescriptions) {
		this(input, Arrays.asList(propertyDescriptions));
	}
	
	public FilterSegmentByPropertyDescription(Pipe input, List<SegmentPropertyDescription> propertyDescriptions) {
		this.input = input;
		this.propertyDescriptions = propertyDescriptions;
	}

	@Override
	public void run() {
		int filterInCount = 0;
		int filterOutCount = 0;
		while (input.hasNext()) {
			Result result = input.getNext();
			Segment segment = result.getSegments().get(0);	// XXX FIXME
			if (isAccepted(segment)) {
				getOutput().put(result);
				filterInCount++;
			} else {
				filterOutCount++;
			}
		}
		logger.info(description() + " filtered: IN " + filterInCount + " OUT " + filterOutCount);
	}
	
	public String description() {
		return "Filter by properties";	// TODO
	}
	
	private boolean isAccepted(Segment segment) {
		for (SegmentPropertyDescription segmentPropertyDescription : propertyDescriptions) {
			if (!segmentPropertyDescription.apply(segment)) {
				return false;
			}
		}
		return true;
	}

}
