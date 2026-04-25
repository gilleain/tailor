package tailor.description.atom;

import java.util.List;

import tailor.api.AtomListCondition;
import tailor.api.AtomListDescription;
import tailor.api.AtomListMeasure;
import tailor.condition.atom.HBondCondition;
import tailor.description.DescriptionPath;
import tailor.description.GroupDescription;
import tailor.measure.HBondMeasure;

public class HBondDescription implements AtomListDescription {
	
	private String name;
	
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
		this("", haDistance, minDHAAngle, maxDHAAngle, 
				acceptorAttachedDescriptionPath, acceptorAttachedDescriptionPath, 
				acceptorAttachedDescriptionPath, acceptorAttachedDescriptionPath);
	}
	
	public HBondDescription(String name, double haDistance, double minDHAAngle, double maxDHAAngle, 
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
		return new HBondCondition(haDistance, minDHAAngle, maxDHAAngle, getDHPaths(), getDHAPaths());
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
