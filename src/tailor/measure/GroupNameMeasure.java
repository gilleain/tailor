package tailor.measure;

import tailor.measurement.PropertyMeasurement;
import tailor.structure.Group;

public class GroupNameMeasure {
	
	public PropertyMeasurement measure(Group group) {
		return new PropertyMeasurement(group.getName());
	}

}
