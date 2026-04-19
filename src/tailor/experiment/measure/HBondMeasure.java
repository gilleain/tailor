package tailor.experiment.measure;

import java.util.ArrayList;
import java.util.List;

import tailor.experiment.api.AtomListMeasure;
import tailor.experiment.api.Measurement;
import tailor.experiment.condition.AtomPartition;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.description.GroupDescription;

public class HBondMeasure implements AtomListMeasure {
	
	private AtomDistanceMeasure atomDistanceMeasure;
	
	private AtomAngleMeasure atomAngleMeasure;

	public HBondMeasure(List<DescriptionPath> atomDistancePaths, List<DescriptionPath> atomAnglePaths) {
		this.atomDistanceMeasure = new AtomDistanceMeasure(atomDistancePaths);
		this.atomAngleMeasure = new AtomAngleMeasure(atomAnglePaths);
	}

	@Override
	public Measurement measure(AtomPartition atomPartition) {
		
		DoubleMeasurement d = this.atomDistanceMeasure.measure(atomPartition);
		DoubleMeasurement a = this.atomAngleMeasure.measure(atomPartition);
		
		return new CompositeMeasurement(d, a);
	}

	@Override
	public List<GroupDescription> getGroupDescriptions() {
		List<GroupDescription> descriptions = new ArrayList<>();
		descriptions.addAll(atomDistanceMeasure.getGroupDescriptions());
		descriptions.addAll(atomAngleMeasure.getGroupDescriptions());
		return descriptions;
	}
}
