package tailor.description.group;

import java.util.Set;

import tailor.condition.AtomPartition;
import tailor.condition.UpperBoundCondition;
import tailor.description.DescriptionPath;
import tailor.measure.CentroidMeasure;
import tailor.measure.PointDistanceMeasure;
import tailor.measurement.DoubleMeasurement;
import tailor.measurement.PointMeasurement;

public class GroupCenterDistanceDescription {
	
	private CentroidMeasure measureA;
	private CentroidMeasure measureB;
	private PointDistanceMeasure measureC;
	private UpperBoundCondition upperBound;
	
	public GroupCenterDistanceDescription(double maxDistance, Set<DescriptionPath> centerA, Set<DescriptionPath> centerB) {
		
	}
	
	public boolean apply(AtomPartition atomPartition) {
		PointMeasurement pA = measureA.measure(null);
		PointMeasurement pB = measureB.measure(null);
		DoubleMeasurement measurement = measureC.measure(pA.getValue(), pB.getValue());
		
		return upperBound.accept(measurement);
	}

}
