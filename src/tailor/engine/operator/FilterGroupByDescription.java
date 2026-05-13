package tailor.engine.operator;

import java.util.List;
import java.util.logging.Logger;

import tailor.api.GroupPropertyDescription;
import tailor.engine.plan.Result;
import tailor.structure.Group;

public class FilterGroupByDescription extends AbstractOperator {
	
	private Logger logger = Logger.getLogger(FilterGroupByDescription.class.getName());
	
	private final GroupPropertyDescription groupPropertyDescription;
	
	public FilterGroupByDescription(GroupPropertyDescription groupPropertyDescription) {
		this.groupPropertyDescription = groupPropertyDescription;
	}

	@Override
	public void run() {
		while (source.hasNext()) {
			Result nextResult = source.getNext();
			List<Group> groups = nextResult.getGroups();
			// TODO
			Group group0 = groups.get(0);
			if (groupPropertyDescription.apply(group0)) {
				logger.info("Accepting " + nextResult);
				sink.put(nextResult);
			}
		}
		
	}

	@Override
	public String description() {
		return "GroupFilter: id[" + getId() + "] on:" + groupPropertyDescription;
	}

}
