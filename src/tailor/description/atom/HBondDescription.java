package tailor.description.atom;

import java.util.List;

import tailor.api.AtomListDescription;
import tailor.api.AtomListMeasure;
import tailor.api.Measurement;
import tailor.condition.AtomPartition;
import tailor.condition.RangeCondition;
import tailor.condition.UpperBoundCondition;
import tailor.description.AtomDescription;
import tailor.description.DescriptionPath;
import tailor.description.GroupDescription;
import tailor.measure.HBondMeasure;
import tailor.measurement.DoubleMeasurement;

/**
 * Hydrogen bond defined by D-H...A-AA :
 * - Distance between H...A less than 2.5 Angstrom
 * - Angle between DHA is +/- 90 and 180 degrees
 */
public class HBondDescription implements AtomListDescription {
	
	private List<String> names;

	private final UpperBoundCondition haDistanceCondition;
	
	private final RangeCondition daaAngleRangeCondition;
	
	private final HBondMeasure measure;
	
	private final DescriptionPath donorDescriptionPath;
	
	private final DescriptionPath hydrogenDescriptionPath;
	
	private final DescriptionPath acceptorDescriptionPath;
	
	private final DescriptionPath acceptorAttachedDescriptionPath;
	
	public HBondDescription(double haDistance, double minDAAAngle, double maxDAAAngle, 
			DescriptionPath donorDescriptionPath, DescriptionPath hydrogenDescriptionPath,
			DescriptionPath acceptorDescriptionPath, DescriptionPath acceptorAttachedDescriptionPath) {
		this(List.of(""), haDistance, minDAAAngle, maxDAAAngle, 
				donorDescriptionPath, hydrogenDescriptionPath, acceptorDescriptionPath, acceptorAttachedDescriptionPath);
	}
	
	public HBondDescription(List<String> names, double haDistance, double minDAAAngle, double maxDAAAngle, 
			DescriptionPath donorDescriptionPath, DescriptionPath hydrogenDescriptionPath,
			DescriptionPath acceptorDescriptionPath, DescriptionPath acceptorAttachedDescriptionPath) {
		this.names = names;
		this.haDistanceCondition = new UpperBoundCondition(haDistance);
		this.daaAngleRangeCondition = new RangeCondition(minDAAAngle, maxDAAAngle);
		
		this.donorDescriptionPath = donorDescriptionPath;
		this.hydrogenDescriptionPath = hydrogenDescriptionPath;
		this.acceptorDescriptionPath = acceptorDescriptionPath;
		this.acceptorAttachedDescriptionPath = acceptorAttachedDescriptionPath;
		
		this.measure = (HBondMeasure) createMeasure();
	}

	private List<DescriptionPath> getDHPaths() {
		return List.of(hydrogenDescriptionPath, acceptorDescriptionPath);
	}
	
	private List<DescriptionPath>  getDAAAPaths() {
		return List.of(donorDescriptionPath, acceptorDescriptionPath, acceptorAttachedDescriptionPath);
	}

	@Override
	public List<GroupDescription> getGroupDescriptions() {
		return List.of(donorDescriptionPath.getGroupDescription(), 
					   hydrogenDescriptionPath.getGroupDescription(), 
					   acceptorDescriptionPath.getGroupDescription(), 
					   acceptorAttachedDescriptionPath.getGroupDescription());
	}
	
	@Override
	public AtomListMeasure createMeasure() {
		return new HBondMeasure(names, getDHPaths(), getDAAAPaths());
	}
	
	public boolean apply(AtomPartition atomPartition) {
		Measurement m = measure.measure(atomPartition);
		return haDistanceCondition.accept(new DoubleMeasurement( m.getValue("d") )) 
				&& daaAngleRangeCondition.accept(new DoubleMeasurement( m.getValue("a")));
	}
	
	public AtomDescription getDonorAtomDescription() {
		return donorDescriptionPath.getAtomDescription();
	}
	
	public AtomDescription getAcceptorAtomDescription() {
		return acceptorDescriptionPath.getAtomDescription();
	}
}
