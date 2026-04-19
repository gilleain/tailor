package tailor.experiment.operator;

import java.util.List;

import tailor.experiment.api.AtomListMeasure;
import tailor.experiment.condition.AtomPartition;
import tailor.experiment.plan.Result;

public class Measurer extends AbstractPipeableOperator {
	
	private List<AtomListMeasure> measures;
	
	public Measurer(List<AtomListMeasure> measures) {
		this.measures = measures;
	}

	@Override
	public void run() {
		while (source.hasNext()) {
			Result nextResult = source.getNext();
			AtomPartition atomPartition = nextResult.getAtomPartition();
			for (AtomListMeasure measure : measures) {
				double measurement = measure.measure(atomPartition);
			}
		}
	}

	@Override
	public String description() {
		return "Measurer";
	}

}
