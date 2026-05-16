package tailor.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import tailor.condition.LabelPartition.Part;
import tailor.structure.Segment;

public class SegmentMatcher {
	
	private Logger logger = Logger.getLogger(SegmentMatcher.class.getName());
	
	public static class Match {
		
		private List<Segment> segments;
		private boolean isComplete;
		
		public Match(List<Segment> segments) {
			this.segments = segments;
		}
		
		public boolean isComplete() {
			return this.isComplete;
		}
		
		public Match setComplete() {
			this.isComplete = true;
			return this;
		}
		
		public List<Segment> getSegments() {
			return this.segments;
		}
		
		public String toString() {
			String output = "|";
			for (Segment segment : segments) {
				output += segment.getType().getTypeString();
				output += "|";
			}
			return isComplete + output;
		}
	}
	
	private LabelPartition atomLabels; // TODO - this is implicitly in order - might want to make that explicit
	
	public SegmentMatcher(LabelPartition atomLabels) {
		// this is a partition of the atom labels by residue group
		// TODO - ordered parts of the partition ... which is also ordered?
		this.atomLabels = atomLabels;
	}
	
	public Optional<Match> containedIn(SegmentPartition other) {
		// check the label partition is contained in the segment partition
		Match match = findMatch(other);
		if (match.isComplete()) {
			logger.fine("MATCH " + match + " for " + other);
			return Optional.of(match);
		} else {
			logger.fine("No match " + this.atomLabels + " to " + other + " " + match);
			return Optional.empty();
		}
	}
	
	private Match findMatch(SegmentPartition result) {
		List<Segment> matches = new ArrayList<>();
		for (int partIndex = 0; partIndex < atomLabels.numberOfParts(); partIndex++) {
			Part part = atomLabels.getPart(partIndex);
			int originalPartIndex = part.getIndex();	// the index of the part in the original order
			List<Segment> resultPart = result.getPart(originalPartIndex);
			int indexFrom = 0;
			for (String label : part.getElements()) {	
				int index = findIndex(label, indexFrom, resultPart);
				if (index == -1) {
					return new Match(matches);
				} else {
					Segment item = resultPart.get(index);
					matches.add(item);
					indexFrom = index + 1;
				}
			}	
		}
		return new Match(matches).setComplete();
	}
	
	// Search for a match in the part, starting from the index 'indexFrom', returning the matching index 
	private int findIndex(String label, int indexFrom, List<Segment> resultPart) {
		int index = 0;
		for (Segment item : resultPart) {
			if (index < indexFrom) {
				index++;
				continue;
			} else {
				if (item.getType().getTypeString().equals(label)) {
					return index;
				}
				index++;
			}
		}
		return -1;
	}
	
	public String toString() {
		return "Matcher(" + atomLabels + ")";
	}
}
