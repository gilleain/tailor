package tailor.experiment.description.atom;

import java.util.List;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.api.AtomListDescription;
import tailor.experiment.api.AtomListMeasure;
import tailor.experiment.condition.HBondCondition;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.measure.HBondMeasure;

public class HBondDescription implements AtomListDescription {
	
	private final double haDistance;
	
	private final double minDHAAngle;

	private final double maxDHAAngle;
	
	private final DescriptionPath donorDescriptionPath;
	
	private final DescriptionPath hydrogenDescriptionPath;
	
	private final DescriptionPath acceptorDescriptionPath;
	
	private final DescriptionPath acceptorAttachedDescriptionPath;
	
	public HBondDescription(double haDistance, double minDHAAngle, double maxDHAAngle, 
			DescriptionPath donorDescriptionPath, DescriptionPath hydrogenDescriptionPath,
			DescriptionPath acceptorDescriptionPath, DescriptionPath acceptorAttachedDescriptionPath) {
		this.haDistance = haDistance;
		this.minDHAAngle = minDHAAngle;
		this.maxDHAAngle = maxDHAAngle;
		this.donorDescriptionPath = donorDescriptionPath;
		this.hydrogenDescriptionPath = hydrogenDescriptionPath;
		this.acceptorDescriptionPath = acceptorDescriptionPath;
		// TODO - not actually using this one!
		this.acceptorAttachedDescriptionPath = acceptorAttachedDescriptionPath;
	}

	@Override
	public AtomListCondition createCondition() {
		return new HBondCondition(getDHPaths(), getDHAPaths(), haDistance, minDHAAngle, maxDHAAngle);
	}
	
	private List<DescriptionPath> getDHPaths() {
		return List.of(hydrogenDescriptionPath, acceptorDescriptionPath);
	}
	
	private List<DescriptionPath>  getDHAPaths() {
		return List.of(donorDescriptionPath, hydrogenDescriptionPath, acceptorDescriptionPath);
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
		return new HBondMeasure(getDHPaths(), getDHAPaths());
	}

}
