package tailor.measure;

import tailor.api.GroupPropertyMeasure;
import tailor.api.Measurement;
import tailor.description.GroupDescription;
import tailor.measurement.PropertyMeasurement;
import tops.translation.model.Group;

public class GroupNameMeasure implements GroupPropertyMeasure {
	
	public Measurement<String> measure(Group group) {
		return new PropertyMeasurement(group.getName());
	}

	@Override
	public GroupDescription getGroupDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
