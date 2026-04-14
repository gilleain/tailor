package tailor.experiment.condition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tailor.experiment.description.DescriptionPath;
import tailor.experiment.description.GroupDescription;

public class LabelPartition {
	
	private final List<List<String>> parts;
	
	public LabelPartition(List<List<String>> parts) {
		this.parts = parts;
	}
	
	public static LabelPartition fromDescriptionPaths(List<DescriptionPath> descriptionPaths) {
		Map<GroupDescription, List<String>> partition = new HashMap<>();
		int index = 0;	// TODO :/ this is needed to preserve original order?
		for (DescriptionPath descriptionPath : descriptionPaths) {
			GroupDescription groupDescription = descriptionPath.getGroupDescription();
			groupDescription.setIndex(index);
			List<String> atomLabels;
			if (partition.containsKey(groupDescription)) {
				atomLabels = partition.get(groupDescription);
			} else {
				atomLabels = new ArrayList<>();
				partition.put(groupDescription, atomLabels);
			}
			atomLabels.add(descriptionPath.getAtomDescription().getLabel());
			index++;
		}
		List<List<String>> parts = new ArrayList<>();
		List<GroupDescription> keys = new ArrayList<>(partition.keySet());
		keys.sort(Comparator.comparing(GroupDescription::getIndex));
		for (GroupDescription groupDescription : keys) {
			parts.add(partition.get(groupDescription));
		}
		return new LabelPartition(parts);
	}
	
	public int totalElements() {
		return parts.stream().map(List::size).reduce(0, Integer::sum);
	}
	
	public int numberOfParts() {
		return this.parts.size();
	}
	
	public List<String> getPart(int index) {
		return this.parts.get(index);
	}
	
	public String toString() {
		return this.parts.toString();
	}

}
