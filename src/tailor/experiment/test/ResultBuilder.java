package tailor.experiment.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tailor.experiment.plan.Result;
import tailor.structure.Atom;
import tailor.structure.Chain;
import tailor.structure.Group;

public class ResultBuilder {
	
	@SuppressWarnings("unchecked")
	private static class BaseResultGroupBuilder<T> {
		// the group number, when accumulating sequentially
		protected int counter = 1;

		protected String chainLabel = "A";

		protected List<List<Group>> groups = new ArrayList<>();

		protected List<List<String>> atomLabels = new ArrayList<>();
		
		
		protected T withGroups(String... groupLabels) {
			List<Group> subGroupList = new ArrayList<>();
			for (String groupLabel : groupLabels) {
				subGroupList.add(new Group(counter, groupLabel));
				counter++;
			}
			groups.add(subGroupList);
			return (T) this;
		}
		
		protected T withGroupNumbers(int... groupNumbers) {
			// implicitly withGroupNumbers has to be called after withGroups ...
			List<Group> groupList = groups.get(groups.size() - 1); // get the last list added
			assert groupNumbers.length == groupList.size();
			for (int index = 0; index < groupNumbers.length; index++) {
				groupList.get(index).setNumber(groupNumbers[index]);
			}
			return (T) this;
		}
		
		protected T withAtom(String atomLabel) {
			this.atomLabels.add(List.of(atomLabel));
			return (T) this;
		}
		
		protected T withAtoms(String... atomLabels) {
			this.atomLabels.add(Arrays.asList(atomLabels));
			return (T) this;
		}
	}
	
	public static class ResultGroupListBuilder extends BaseResultGroupBuilder<ResultGroupListBuilder> {
		
		public List<Result> build() {
			List<Result> results = new ArrayList<>();
			
			Chain chain = new Chain(chainLabel);
			assert atomLabels.size() == groups.size();	// should be one set of atoms for each group
				
			for (List<Group> subGroupList : groups) {
				Group firstGroup = subGroupList.get(0);
				List<Atom> atomsForGroup = atomLabels.get(0).stream().map(Atom::new).toList();
				results.add(new Result(chain, firstGroup, atomsForGroup));
			}
				
			
			return results;
		}
		
	}
	
	public static class ResultGroupBuilder extends BaseResultGroupBuilder<ResultGroupBuilder> {
		
		public ResultGroupBuilder startAt(int seqStart) {
			this.counter = seqStart;
			return this;
		}
		
		public Result build() {
			Result result = new Result(new Chain(chainLabel));
			
			assert atomLabels.size() == groups.size();	// should be one set of atoms for each group
				
			int index = 0;
			for (List<Group> subGroupList : groups) {
				for (Group group : subGroupList) {
					List<Atom> atomsForGroup = atomLabels.get(index).stream().map(Atom::new).toList();
					result.add(group, atomsForGroup);
				}
				index++;
			}
			
			return result;
		}
	}
	
	public static ResultGroupBuilder result() {
		return new ResultGroupBuilder();
	}
	
	public static ResultGroupListBuilder resultList() {
		return new ResultGroupListBuilder();
	}

}
