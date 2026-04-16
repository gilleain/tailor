package tailor.experiment.description.atom;

import java.util.Arrays;
import java.util.List;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.api.AtomListDescription;
import tailor.experiment.condition.AtomMatcher;
import tailor.experiment.condition.HBondCondition;
import tailor.experiment.condition.LabelPartition;
import tailor.experiment.description.AtomDescription;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.description.GroupDescription;

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
		AtomMatcher haMatcher = new AtomMatcher(
				new LabelPartition(List.of(
							getPart(hydrogenDescriptionPath), getPart(acceptorDescriptionPath)
						)
				)
		);
						
		AtomMatcher angleMatcher = new AtomMatcher(new LabelPartition(List.of(
				getPart(donorDescriptionPath, hydrogenDescriptionPath), 
				getPart(acceptorDescriptionPath)
			)
		));
		
		return new HBondCondition(haMatcher, angleMatcher, haDistance, minDHAAngle, maxDHAAngle);
	}
	
	private List<String> getPart(DescriptionPath... descriptions) {
		return Arrays.stream(descriptions).map(DescriptionPath::getAtomDescription).map(AtomDescription::getLabel).toList();
	}

	@Override
	public boolean isForSameGroup() {
		return false;
	}

	@Override
	public GroupDescription getFirstGroupDescription() {
		return null;	// TODO
	}

	@Override
	public List<GroupDescription> getGroupDescriptions() {
		return List.of(donorDescriptionPath.getGroupDescription(), 
					   hydrogenDescriptionPath.getGroupDescription(), 
					   acceptorDescriptionPath.getGroupDescription(), 
					   acceptorAttachedDescriptionPath.getGroupDescription());
	}

}
