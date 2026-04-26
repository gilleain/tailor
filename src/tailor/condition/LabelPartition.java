package tailor.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import tailor.description.DescriptionPath;
import tailor.description.GroupDescription;

public class LabelPartition {
	
	public interface Part {
		int getIndex();
		int getSize();
		List<String> getElements();
	}
	
	// part with no members, for clarity
	public static class EmptyPart implements Part {
		int index;
		
		public EmptyPart(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return this.index;
		}
		
		public int getSize() {
			return 0;
		}
		
		public List<String> getElements() {
			return List.of();
		}
		
		public String toString() {
			return index + ":[]";
		}
	}	
	
	public static class LabelledPart extends EmptyPart {
		private List<String> elements;
		
		public LabelledPart(int index, List<String> elements) {
			super(index);
			this.elements = elements;
		}
		
		public int getSize() {
			return elements.size();
		}
		
		public List<String> getElements() {
			return this.elements;
		}
		
		public String toString() {
			return index + ":[" +  elements.stream().collect(Collectors.joining(",")) + "]";
		}
		
	}
	
	private final List<Part> parts;
	
	@SafeVarargs
	public LabelPartition(List<String>... partsAsStrings) {
		// TODO - this uses the order of the parts as given, which is risky ..
		this.parts = new ArrayList<>();
		int index = 0;
		for (List<String> partAsStrings : partsAsStrings) {
			if (partAsStrings.isEmpty()) {
				parts.add(new EmptyPart(index));
			} else {
				parts.add(new LabelledPart(index, partAsStrings));
			}
		}
	}
	
	public LabelPartition(List<Part> parts) {
		this.parts =  parts;
	}

	public static LabelPartition fromDescriptionPaths(DescriptionPath... descriptionPaths) {
		return LabelPartition.fromDescriptionPaths(Arrays.asList(descriptionPaths));
	}
	
	public static LabelPartition fromDescriptionPaths(List<DescriptionPath> descriptionPaths) {
		Map<GroupDescription, List<String>> partition = new LinkedHashMap<>();
		
		for (DescriptionPath descriptionPath : descriptionPaths) {
			GroupDescription groupDescription = descriptionPath.getGroupDescription();
			List<String> part = partition.computeIfAbsent(groupDescription, _ -> new ArrayList<>());
			part.add(descriptionPath.getAtomDescription().getLabel());
		}
		List<Part> parts = new ArrayList<>();
		for (GroupDescription groupDescription : partition.keySet()) {
			parts.add(new LabelledPart(groupDescription.getIndex(), partition.get(groupDescription)));
		}

		return new LabelPartition(parts);
	}
	
	public int totalElements() {
		return parts.stream().map(Part::getSize).reduce(0, Integer::sum);
	}
	
	public int numberOfParts() {
		return this.parts.size();
	}
	
	public Part getPart(int index) {
		return this.parts.get(index);
	}
	
	public boolean equals(Object o) {
		if (o instanceof LabelPartition other) {
			for (int index = 0; index < parts.size(); index++) {
				if (index > other.parts.size() - 1) return false;
				List<String> otherElements = other.parts.get(index).getElements();
				if (!parts.get(index).getElements().equals(otherElements)) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}
	
	public String toString() {
		return this.parts.toString();
	}

}
