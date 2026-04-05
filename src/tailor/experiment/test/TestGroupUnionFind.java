package tailor.experiment.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tailor.experiment.api.AtomListDescription;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.plan.GroupUnionFind;

public class TestGroupUnionFind {
	
	@Test
	public void testTwoPairs() {
		List<GroupDescription> groups = new ArrayList<>();
		GroupUnionFind uf = new GroupUnionFind(groups);
		
		List<AtomListDescription> atomListDescriptions = new ArrayList<>();
		uf.union(atomListDescriptions);
		
		for (List<GroupDescription> components : uf.getComponents()) {
			System.out.println(components);
		}
	}

}
