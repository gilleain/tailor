package tops.translation.experimental;

import java.util.List;
import java.util.logging.Logger;

import tops.translation.model.Chain;
import tops.translation.model.Segment;

public class SegmentSource extends AbstractOperator {
	
	private static Logger logger = Logger.getLogger(SegmentSource.class.getName());
	
	private final Chain chain;
	
	private final List<Pipe> outputs;
	
	public SegmentSource(Chain chain, List<Pipe> outputs) {
		this.chain = chain;
		this.outputs = outputs;
	}

	@Override
	public void run() {
		for (Segment segment : chain.getSegments()) {
			for (Pipe output : outputs) {
//				logger.info("Putting " + segment + " to " + output.getSinkId());
				output.put(new Result(chain, segment));
			}
		}
	}
}
