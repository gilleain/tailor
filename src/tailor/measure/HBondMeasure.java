package tailor.measure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tailor.api.AtomListMeasure;
import tailor.api.Measurement;
import tailor.condition.AtomPartition;
import tailor.description.DescriptionPath;
import tailor.description.GroupDescription;

public class HBondMeasure implements AtomListMeasure {
	
	private List<String> names;	// names of each part of the measure
	
	private AtomDistanceMeasure atomDistanceMeasure;
	
	private AtomAngleMeasure atomAngleMeasure;

	public HBondMeasure(List<DescriptionPath> atomDistancePaths, List<DescriptionPath> atomAnglePaths) {
		this(List.of("HBond"), atomDistancePaths, atomAnglePaths);
	}

	public HBondMeasure(List<String> names, List<DescriptionPath> atomDistancePaths, List<DescriptionPath> atomAnglePaths) {
		this.names = names;
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

	@Override
	public String getName() {
		return names.stream().collect(Collectors.joining("\t"));
	}
}
