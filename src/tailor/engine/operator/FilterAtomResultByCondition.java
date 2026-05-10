package tailor.engine.operator;

import java.util.List;
import java.util.logging.Logger;

import tailor.api.AtomListDescription;
import tailor.condition.AtomPartition;
import tailor.engine.plan.Result;

public class FilterAtomResultByCondition extends AbstractOperator {
	
	private Logger logger = Logger.getLogger(FilterAtomResultByCondition.class.getName());
	
	private List<AtomListDescription> listDescriptions;
	
	public FilterAtomResultByCondition(List<AtomListDescription> listDescriptions) {
		this.listDescriptions = listDescriptions;
	}
	
	public void run() {
		int filterInCount = 0;
		int filterOutCount = 0;
		while (source.hasNext()) {
			Result nextResult = source.getNext();
			AtomPartition atomPartition = nextResult.getAtomPartition();
			if (isAccepted(atomPartition)) {
				logger.fine("Filtering IN " + nextResult);
				sink.put(nextResult);
				filterInCount++;
			} else {
				logger.fine("Filtering OUT " + nextResult);
				filterOutCount++;
			}
		}
		logger.info(description() + " filtered: IN " + filterInCount + " OUT " + filterOutCount);
	}
	
	private boolean isAccepted(AtomPartition atomPartition) {
		for (AtomListDescription listDescription : listDescriptions) {
			if (!listDescription.apply(atomPartition)) {
				logger.fine(listDescription + " failed for " + atomPartition);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String description() {
		List<String> listDescriptionNames = listDescriptions.stream().map(c -> c.getClass().getSimpleName()).toList();
		return "Filter: id[" + getId() + "] on:" + listDescriptionNames;
	}
	
}
