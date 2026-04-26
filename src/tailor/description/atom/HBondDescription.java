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
	
	private List<String> names;
	
	private final double haDistance;
	
	private final double minDAAAngle;

	private final double maxDAAAngle;
	
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
		this.haDistance = haDistance;
		this.minDAAAngle = minDAAAngle;
		this.maxDAAAngle = maxDAAAngle;
		this.donorDescriptionPath = donorDescriptionPath;
		this.hydrogenDescriptionPath = hydrogenDescriptionPath;
		this.acceptorDescriptionPath = acceptorDescriptionPath;
		this.acceptorAttachedDescriptionPath = acceptorAttachedDescriptionPath;
	}

	@Override
	public AtomListCondition createCondition() {
		return new HBondCondition(haDistance, minDAAAngle, maxDAAAngle, getDHPaths(), getDAAAPaths());
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

}
