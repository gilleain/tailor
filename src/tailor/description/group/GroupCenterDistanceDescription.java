package tailor.description.group;

import java.util.List;

import tailor.condition.UpperBoundCondition;
import tailor.description.GroupDescriptionPath;
import tailor.measure.CentroidMeasure;
import tailor.measure.PointDistanceMeasure;
import tailor.measurement.DoubleMeasurement;
import tailor.measurement.PointMeasurement;
import tailor.partition.GroupPartition;

public class GroupCenterDistanceDescription {
	
	private CentroidMeasure measureA;
	private CentroidMeasure measureB;
	private PointDistanceMeasure measureC;
	private UpperBoundCondition upperBound;
	
	public GroupCenterDistanceDescription(
			double maxDistance, List<GroupDescriptionPath> centerA, List<GroupDescriptionPath> centerB) {
		this.measureA = new CentroidMeasure(centerA);
		this.measureB = new CentroidMeasure(centerB);
		this.measureC = new PointDistanceMeasure();
	}
	
	public boolean apply(GroupPartition groupPartition) {
		PointMeasurement pA = measureA.measure(groupPartition);
		PointMeasurement pB = measureB.measure(groupPartition);
		DoubleMeasurement measurement = measureC.measure(pA.getValue(), pB.getValue());
		
		return measurement.apply(upperBound);
	}

}
