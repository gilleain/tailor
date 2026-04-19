package tailor.experiment.test;

import static tailor.experiment.test.DescriptionBuilder.group;
import static tailor.experiment.test.Helper.pathTo;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tailor.api.AtomListDescription;
import tailor.engine.plan.GroupUnionFind;
import tailor.engine.plan.GroupUnionFind.Component;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.description.atom.AtomDistanceDescription;

public class TestGroupUnionFind {
	
	@Test
	public void testTwoConnectedOneDisconnected() {
		GroupDescription group1 = group().withGroupLabel("GLY").withAtomLabels("O").build();
		GroupDescription group2 = group().withGroupLabel("SER").withAtomLabels("O").build();
		GroupDescription group3 = group().withGroupLabel("HIS").withAtomLabels("O").build();
		List<GroupDescription> groups = List.of(group1, group2, group3);
		
		GroupUnionFind uf = new GroupUnionFind(groups);
		
		List<AtomListDescription> atomListDescriptions = new ArrayList<>();
		atomListDescriptions.add(new AtomDistanceDescription(1, pathTo(group1, "O"), pathTo(group2, "O")));
		uf.union(atomListDescriptions, List.of());
		
		for (Component component : uf.getComponents()) {
			System.out.println(component);
		}
	}
	
	@Test
	public void testTwoConnectedPairs() {
		GroupDescription group1 = group().withGroupLabel("GLY").withAtomLabels("O").build();
		GroupDescription group2 = group().withGroupLabel("SER").withAtomLabels("O").build();
		GroupDescription group3 = group().withGroupLabel("HIS").withAtomLabels("O").build();
		List<GroupDescription> groups = List.of(group1, group2, group3);
		
		GroupUnionFind uf = new GroupUnionFind(groups);
		
		List<AtomListDescription> atomListDescriptions = new ArrayList<>();
		atomListDescriptions.add(new AtomDistanceDescription(1, pathTo(group1, "O"), pathTo(group2, "O")));
		atomListDescriptions.add(new AtomDistanceDescription(1, pathTo(group2, "O"), pathTo(group3, "O")));
		uf.union(atomListDescriptions, List.of());
		
		for (Component component : uf.getComponents()) {
			System.out.println(component);
		}
	}
	
	@Test
	public void testTwoDisconnectedPairs() {
		GroupDescription group1 = group().withGroupLabel("GLY").withAtomLabels("O").build();
		GroupDescription group2 = group().withGroupLabel("SER").withAtomLabels("O").build();
		GroupDescription group3 = group().withGroupLabel("HIS").withAtomLabels("O").build();
		GroupDescription group4 = group().withGroupLabel("TYR").withAtomLabels("O").build();
		List<GroupDescription> groups = List.of(group1, group2, group3, group4);
		
		GroupUnionFind uf = new GroupUnionFind(groups);
		
		List<AtomListDescription> atomListDescriptions = new ArrayList<>();
		atomListDescriptions.add(new AtomDistanceDescription(1, pathTo(group1, "O"), pathTo(group2, "O")));
		atomListDescriptions.add(new AtomDistanceDescription(1, pathTo(group3, "O"), pathTo(group4, "O")));
		uf.union(atomListDescriptions, List.of());
		
		for (Component component : uf.getComponents()) {
			System.out.println(component);
		}
	}
}
