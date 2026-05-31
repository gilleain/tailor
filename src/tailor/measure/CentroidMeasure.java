package tailor.measure;

import java.util.Set;

import tailor.description.GroupDescriptionPath;
import tailor.measurement.PointMeasurement;
import tailor.partition.GroupPartition;

public class CentroidMeasure {
	
	private Set<GroupDescriptionPath> paths;
	
	public CentroidMeasure(Set<GroupDescriptionPath> paths) {
		this.paths = paths;
	}


	public PointMeasurement measure(GroupPartition groups) {
		// TODO
		return null;
	}

}
