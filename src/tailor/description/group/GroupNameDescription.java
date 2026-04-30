package tailor.description.group;

import tailor.condition.PropertyEquals;
import tailor.measure.GroupNameMeasure;
import tailor.measurement.PropertyMeasurement;
import tailor.structure.Group;

public class GroupNameDescription {
	
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
		PropertyMeasurement propertyMeasurement = groupNameMeasure.measure(group);
		return propertyEquals.accept(propertyMeasurement);
	}

}
