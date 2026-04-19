package tailor.experiment.measure;

import tailor.experiment.api.AtomListMeasure;
import tailor.experiment.api.Measurement;
import tailor.experiment.condition.AtomMatcher;
import tailor.experiment.condition.AtomPartition;

public class HBondMeasure implements AtomListMeasure {
	
	private AtomDistanceMeasure atomDistanceMeasure;
	
	private AtomAngleMeasure atomAngleMeasure;

	public HBondMeasure(AtomMatcher atomDistanceMatcher, AtomMatcher atomAngleMatcher) {
		this.atomDistanceMeasure = new AtomDistanceMeasure(atomDistanceMatcher);
		this.atomAngleMeasure = new AtomAngleMeasure(atomAngleMatcher);
	}

	@Override
	public Measurement measure(AtomPartition atomPartition) {
		
		DoubleMeasurement d = this.atomDistanceMeasure.measure(atomPartition);
		DoubleMeasurement a = this.atomAngleMeasure.measure(atomPartition);
		
		return new CompositeMeasurement(d, a);
	}
}
