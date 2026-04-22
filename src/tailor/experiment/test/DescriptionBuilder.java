package tailor.experiment.test;

import java.util.Arrays;
import java.util.List;

import tailor.description.AtomDescription;
import tailor.description.GroupDescription;

public class DescriptionBuilder {
	
	public static class GroupBuilder {
		private String groupLabel = null;
		private List<String> atomLabels;
		
		public GroupBuilder withGroupLabel(String label) {
			this.groupLabel = label;
			return this;
		}
		
		public GroupBuilder withAtomLabels(String... labels) {
			this.atomLabels = Arrays.asList(labels);
			return this;
		}
		
		public GroupDescription build() {
			GroupDescription groupDescription = new GroupDescription(groupLabel);
			for (String atomLabel : atomLabels) {
				groupDescription.addAtomDescription(new AtomDescription(atomLabel));
			}
			return groupDescription;
		}
		
	}
	
	public static GroupBuilder group() {
		return new GroupBuilder();
	}

}
