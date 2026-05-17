package tailor.operator;

import static org.junit.Assert.assertEquals;
import static tailor.description.DescriptionFactory.group;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import tailor.condition.LabelPartition;
import tailor.condition.LabelPartition.LabelledPart;
import tailor.description.AtomDescription;
import tailor.description.GroupDescriptionPath;
import tailor.description.GroupDescription;

public class TestLabelPartition {
	
	@Test
	public void testFromTwoDescriptionPaths() {
		GroupDescriptionPath dp1 = new GroupDescriptionPath(group().withIndex(0).build(), new AtomDescription("O"));
		GroupDescriptionPath dp2 = new GroupDescriptionPath(group().withIndex(2).build(), new AtomDescription("C"));
		
		LabelPartition l = LabelPartition.fromDescriptionPaths(List.of(dp1, dp2));
		System.out.println(l);
		assertEquals("2 parts", 2, l.numberOfParts());
		assertEquals(new LabelPartition(List.of(l(0, "O"), l(2, "C"))), l);
	}
	
	@Test
	public void testFromThreeDescriptionPaths() {
		GroupDescriptionPath dp1 = new GroupDescriptionPath(group().withIndex(0).build(), new AtomDescription("N"));
		GroupDescriptionPath dp2 = new GroupDescriptionPath(group().withIndex(2).build(), new AtomDescription("C"));
		GroupDescriptionPath dp3 = new GroupDescriptionPath(group().withIndex(5).build(), new AtomDescription("O"));
		
		LabelPartition l = LabelPartition.fromDescriptionPaths(List.of(dp1, dp2, dp3));
		System.out.println(l);
		assertEquals("3 parts", 3, l.numberOfParts());
		assertEquals(new LabelPartition(List.of(l(0, "N"), l(2, "C"), l(5, "O"))), l);
	}
	
	@Test
	public void testFromEmptyInitial() {
		GroupDescriptionPath dp1 = new GroupDescriptionPath(group().withIndex(1).build(), new AtomDescription("N"));
		GroupDescriptionPath dp2 = new GroupDescriptionPath(group().withIndex(2).build(), new AtomDescription("C"));
		
		LabelPartition l = LabelPartition.fromDescriptionPaths(List.of(dp1, dp2));
		System.out.println(l);
		assertEquals("2 parts", 2, l.numberOfParts());
		assertEquals(new LabelPartition(List.of(l(1, "N"), l(2, "C"))), l);
	}
	
	@Test
	public void testFromOutOfOrderPaths() {
		GroupDescription gp1 = group().withIndex(4).build();
		GroupDescription gp2 = group().withIndex(0).build();
		GroupDescriptionPath dp1 = new GroupDescriptionPath(gp1, new AtomDescription("C"));
		GroupDescriptionPath dp2 = new GroupDescriptionPath(gp1, new AtomDescription("O"));
		GroupDescriptionPath dp3 = new GroupDescriptionPath(gp2, new AtomDescription("H"));
		GroupDescriptionPath dp4 = new GroupDescriptionPath(gp2, new AtomDescription("N"));
		
		LabelPartition l = LabelPartition.fromDescriptionPaths(List.of(dp1, dp2, dp3, dp4));
		System.out.println(l);
		assertEquals("2 parts", 2, l.numberOfParts());
		assertEquals(new LabelPartition(List.of(l(4, "C", "O"), l(2, "H", "N"))), l);
	}
	
	private LabelledPart l(int index, String... labels) {
		return new LabelledPart(index, Arrays.asList(labels));
	}
	
}
