package tailor.description.atom;

import java.util.List;

import tailor.api.AtomListDescription;
import tailor.api.AtomListMeasure;
import tailor.condition.AtomPartition;
import tailor.condition.RangeCondition;
import tailor.condition.UpperBoundCondition;
import tailor.description.AtomDescription;
import tailor.description.GroupDescriptionPath;
import tailor.description.GroupDescription;
import tailor.measure.HBondMeasure;
import tailor.measurement.CompositeDoubleMeasurement;

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
	
	private final GroupDescriptionPath donorDescriptionPath;
	
	private final GroupDescriptionPath hydrogenDescriptionPath;
	
	private final GroupDescriptionPath acceptorDescriptionPath;
	
	private final GroupDescriptionPath acceptorAttachedDescriptionPath;
	
	public HBondDescription(double haDistance, double minDAAAngle, double maxDAAAngle, 
			GroupDescriptionPath donorDescriptionPath, GroupDescriptionPath hydrogenDescriptionPath,
			GroupDescriptionPath acceptorDescriptionPath, GroupDescriptionPath acceptorAttachedDescriptionPath) {
		this(List.of(""), haDistance, minDAAAngle, maxDAAAngle, 
				donorDescriptionPath, hydrogenDescriptionPath, acceptorDescriptionPath, acceptorAttachedDescriptionPath);
	}
	
	public HBondDescription(List<String> names, double haDistance, double minDAAAngle, double maxDAAAngle, 
			GroupDescriptionPath donorDescriptionPath, GroupDescriptionPath hydrogenDescriptionPath,
			GroupDescriptionPath acceptorDescriptionPath, GroupDescriptionPath acceptorAttachedDescriptionPath) {
		this.names = names;
		this.haDistanceCondition = new UpperBoundCondition(haDistance);
		this.daaAngleRangeCondition = new RangeCondition(minDAAAngle, maxDAAAngle);
		
		this.donorDescriptionPath = donorDescriptionPath;
		this.hydrogenDescriptionPath = hydrogenDescriptionPath;
		this.acceptorDescriptionPath = acceptorDescriptionPath;
		this.acceptorAttachedDescriptionPath = acceptorAttachedDescriptionPath;
		
		this.measure = (HBondMeasure) createMeasure();
	}

	private List<GroupDescriptionPath> getDHPaths() {
		return List.of(hydrogenDescriptionPath, acceptorDescriptionPath);
	}
	
	private List<GroupDescriptionPath>  getDAAAPaths() {
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
		CompositeDoubleMeasurement m = (CompositeDoubleMeasurement) measure.measure(atomPartition);
		return m.apply("d", haDistanceCondition) && m.apply("a", daaAngleRangeCondition);
	}
	
	public AtomDescription getDonorAtomDescription() {
		return donorDescriptionPath.getAtomDescription();
	}
	
	public AtomDescription getAcceptorAtomDescription() {
		return acceptorDescriptionPath.getAtomDescription();
	}
}
