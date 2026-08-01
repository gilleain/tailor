package tailor.measure;

import java.util.List;
import java.util.Optional;

import javax.vecmath.Point3d;

import tailor.description.GroupDescriptionPath;
import tailor.geometry.Geometer;
import tailor.measurement.PointMeasurement;
import tailor.partition.GroupMatcher;
import tailor.partition.GroupMatcher.Match;
import tailor.partition.GroupPartition;
import tailor.partition.LabelPartition;
import tailor.structure.Atom;
import tailor.structure.Group;

public class CentroidMeasure {
	
	private GroupMatcher groupMatcher;
	
	private List<GroupDescriptionPath> paths;
	
	public CentroidMeasure(List<GroupDescriptionPath> paths) {
		this.paths = paths;
		this.groupMatcher = new GroupMatcher(LabelPartition.fromDescriptionPaths(paths));
	}

	public PointMeasurement measure(GroupPartition groupPartition) {
		Optional<Match> match = groupMatcher.containedIn(groupPartition);
		if (match.isPresent()) {
			List<Group> groups = match.get().getGroups();
			List<Atom> atoms = groups.stream().map(Group::getAtoms).flatMap(List::stream).toList();
			Point3d center = Geometer.averagePoints(atoms.stream().map(Atom::getCenter).toList());
			return new PointMeasurement(center);
		}
		return null;
	}

}
