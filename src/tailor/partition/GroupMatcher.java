package tailor.partition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import tailor.partition.LabelPartition.Part;
import tailor.structure.Group;

public class GroupMatcher {
	
	private Logger logger = Logger.getLogger(GroupMatcher.class.getName());
	
	public static class Match {
		
		private List<Group> groups;
		private boolean isComplete;
		
		public Match(List<Group> groups) {
			this.groups = groups;
		}
		
		public boolean isComplete() {
			return this.isComplete;
		}
		
		public Match setComplete() {
			this.isComplete = true;
			return this;
		}
		
		public List<Group> getGroups() {
			return this.groups;
		}
		
		public String toString() {
			String groupLabels = "|";
			for (Group group : groups) {
				groupLabels += group.getName();
				groupLabels += "|";
			}
			return isComplete + groupLabels;
		}
	}
	
	private LabelPartition groupLabels; // TODO - this is implicitly in order - might want to make that explicit
	
	public GroupMatcher(LabelPartition groupLabels) {
		// this is a partition of the group labels by chain
		// TODO - ordered parts of the partition ... which is also ordered?
		this.groupLabels = groupLabels;
	}
	
	public Optional<Match> containedIn(GroupPartition other) {
		// check the label partition is contained in the atom partition
		Match match = findMatch(other);
		if (match.isComplete()) {
			logger.fine("MATCH " + match + " for " + other);
			return Optional.of(match);
		} else {
			logger.fine("No match " + this.groupLabels + " to " + other + " " + match);
			return Optional.empty();
		}
	}
	
	private Match findMatch(GroupPartition resultGroups) {
		List<Group> groupMatches = new ArrayList<>();
		for (int partIndex = 0; partIndex < groupLabels.numberOfParts(); partIndex++) {
			Part part = groupLabels.getPart(partIndex);
			int originalPartIndex = part.getIndex();	// the index of the part in the original order
			List<Group> groupPart = resultGroups.getPart(originalPartIndex);
			for (String groupLabel : part.getElements()) {	
				Group group = findGroup(groupLabel, groupPart);
				if (group == null) {
					return new Match(groupMatches);
				} else {
					groupMatches.add(group);
				}
			}	
		}
		return new Match(groupMatches).setComplete();
	}
	
	private Group findGroup(String label, List<Group> groups) {
		for (Group group : groups) {
			if (group.getName().equals(label)) {
				return group;
			}
		}
		return null;
	}
	
	public String toString() {
		return "Matcher(" + groupLabels + ")";
	}
}
