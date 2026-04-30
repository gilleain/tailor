package tailor.engine.operator;

import java.util.List;
import java.util.logging.Logger;

import tailor.description.group.GroupNameDescription;
import tailor.engine.plan.Result;
import tailor.structure.Group;

public class FilterGroupByDescription extends AbstractPipeableOperator {
	
	private Logger logger = Logger.getLogger(FilterGroupByDescription.class.getName());
	
	private final GroupNameDescription groupNameDescription;
	
	public FilterGroupByDescription(GroupNameDescription groupNameDescription) {
		this.groupNameDescription = groupNameDescription;
	}

	@Override
	public void run() {
		while (source.hasNext()) {
			Result nextResult = source.getNext();
			List<Group> groups = nextResult.getGroups();
			// TODO
			Group group0 = groups.get(0);
			if (groupNameDescription.apply(group0)) {
				sink.put(nextResult);
			}
		}
		
	}

	@Override
	public String description() {
		return "GroupFilter: id[" + getId() + "] on:" + groupNameDescription;
	}

}
