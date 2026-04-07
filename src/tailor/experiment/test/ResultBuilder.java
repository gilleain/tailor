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

		protected List<Group> groups = new ArrayList<>();

		protected List<String> atomLabels = new ArrayList<>();
		
		
		protected T withGroups(String... groupLabels) {
			for (String groupLabel : groupLabels) {
				groups.add(new Group(counter, groupLabel));
				counter++;
			}
			return (T) this;
		}
		
		protected T withAtom(String atomLabel) {
			this.atomLabels.add(atomLabel);
			return (T) this;
		}
		
		protected T withAtoms(String... atomLabels) {
			this.atomLabels.addAll(Arrays.asList(atomLabels));
			return (T) this;
		}
	}
	
	public static class ResultGroupListBuilder extends BaseResultGroupBuilder<ResultGroupListBuilder> {
		
		public List<Result> build() {
			List<Result> results = new ArrayList<>();
			
			Chain chain = new Chain(chainLabel);
			if (atomLabels.size() == 1) {
				// All the same atom
				String atomLabel = atomLabels.get(0);
				for (Group group : groups) {
					results.add(new Result(chain, group, new Atom(atomLabel)));
				}
			} else {
				assert atomLabels.size() == groups.size();	// should be one for each
				for (int index = 0; index < atomLabels.size(); index++) {
					results.add( new Result(chain, groups.get(index), new Atom(atomLabels.get(index))));
				}
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
			Result result = new Result(new Chain(chainLabel), groups);
			if (atomLabels.size() == 1) {
				// All the same atom
				String atomLabel = atomLabels.get(0);
				for (Group group : groups) {
					result.addAtomToGroup(group, new Atom(atomLabel));
				}
			} else {
				if (groups.size() == 1) {	// one group, multiple atoms
					for (String atomLabel : atomLabels) {
						result.addAtomToGroup(groups.get(0), new Atom(atomLabel));
					}
				} else {					// multiple groups, one atom each
					assert atomLabels.size() == groups.size();
					for (int index = 0; index < atomLabels.size(); index++) {
						result.addAtomToGroup(groups.get(index), new Atom(atomLabels.get(index)));
					}
				}
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
