package tailor.description.group;

import tailor.api.GroupPropertyDescription;
import tailor.api.Measurement;
import tailor.condition.PropertyEquals;
import tailor.description.GroupDescription;
import tailor.measure.GroupNameMeasure;
import tailor.structure.Group;

public class GroupNameDescription implements GroupPropertyDescription {
	
	private final PropertyEquals propertyEquals;
	
	private final GroupNameMeasure groupNameMeasure;
	
	public GroupNameDescription(PropertyEquals propertyEquals, GroupNameMeasure groupNameMeasure) {
		this.propertyEquals = propertyEquals;
		this.groupNameMeasure = groupNameMeasure;
	}
	
	public GroupNameMeasure createMeasure() {
		return this.groupNameMeasure;
	}
	
	public boolean apply(Group group) {
		Measurement<String> propertyMeasurement = groupNameMeasure.measure(group);
		return propertyMeasurement.apply(propertyEquals);
	}
	
	public String toString() {
		return "GroupName" + this.propertyEquals.toString();
	}

	@Override
	public GroupDescription getGroupDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
