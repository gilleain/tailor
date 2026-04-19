package tailor.condition;

import java.util.ArrayList;
import java.util.Arrays;
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

	public static LabelPartition fromDescriptionPaths(DescriptionPath... descriptionPaths) {
		return LabelPartition.fromDescriptionPaths(Arrays.asList(descriptionPaths));
	}
	
	// TODO - calling this method twice on the same list of paths reverses the matcher!!
	public static LabelPartition fromDescriptionPaths(List<DescriptionPath> descriptionPaths) {
		Map<GroupDescription, List<String>> partition = new HashMap<>();
		
		for (DescriptionPath descriptionPath : descriptionPaths) {
			GroupDescription groupDescription = descriptionPath.getGroupDescription();
			List<String> atomLabels;
			if (partition.containsKey(groupDescription)) {
				atomLabels = partition.get(groupDescription);
			} else {
				atomLabels = new ArrayList<>();
				partition.put(groupDescription, atomLabels);
			}
			atomLabels.add(descriptionPath.getAtomDescription().getLabel());
		}
		List<List<String>> parts = new ArrayList<>();
		List<GroupDescription> keys = new ArrayList<>(partition.keySet());
		keys.sort(Comparator.comparing(GroupDescription::getIndex));
		
		int index = 0;
		for (GroupDescription groupDescription : keys) {
			int groupIndex = groupDescription.getIndex();
			
			if (groupIndex > index) {
				while (groupIndex != index) {
					parts.add(new ArrayList<>());
					index++;
				}
			}
			parts.add(partition.get(groupDescription));
			index++;
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
