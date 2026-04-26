package tailor.operator;

import static org.junit.Assert.assertEquals;
import static tailor.description.DescriptionFactory.group;

import java.util.List;

import org.junit.Test;

import tailor.condition.LabelPartition;
import tailor.description.AtomDescription;
import tailor.description.DescriptionPath;
import tailor.description.GroupDescription;

public class TestLabelPartition {
	
	@Test
	public void testFromTwoDescriptionPaths() {
		DescriptionPath dp1 = new DescriptionPath(group().withIndex(0).build(), new AtomDescription("O"));
		DescriptionPath dp2 = new DescriptionPath(group().withIndex(2).build(), new AtomDescription("C"));
		
		LabelPartition l = LabelPartition.fromDescriptionPaths(List.of(dp1, dp2));
		System.out.println(l);
		assertEquals("3 parts", 3, l.numberOfParts());
	}
	
	@Test
	public void testFromThreeDescriptionPaths() {
		DescriptionPath dp1 = new DescriptionPath(group().withIndex(0).build(), new AtomDescription("N"));
		DescriptionPath dp2 = new DescriptionPath(group().withIndex(2).build(), new AtomDescription("C"));
		DescriptionPath dp3 = new DescriptionPath(group().withIndex(5).build(), new AtomDescription("O"));
		
		LabelPartition l = LabelPartition.fromDescriptionPaths(List.of(dp1, dp2, dp3));
		System.out.println(l);
		assertEquals("6 parts", 6, l.numberOfParts());
	}
	
	@Test
	public void testFromEmptyInitial() {
		DescriptionPath dp1 = new DescriptionPath(group().withIndex(1).build(), new AtomDescription("N"));
		DescriptionPath dp2 = new DescriptionPath(group().withIndex(2).build(), new AtomDescription("C"));
		
		LabelPartition l = LabelPartition.fromDescriptionPaths(List.of(dp1, dp2));
		System.out.println(l);
		assertEquals("3 parts", 3, l.numberOfParts());
	}
	
	@Test
	public void testFromOutOfOrderPaths() {
		GroupDescription gp1 = group().withIndex(4).build();
		GroupDescription gp2 = group().withIndex(0).build();
		DescriptionPath dp1 = new DescriptionPath(gp1, new AtomDescription("C"));
		DescriptionPath dp2 = new DescriptionPath(gp1, new AtomDescription("O"));
		DescriptionPath dp3 = new DescriptionPath(gp2, new AtomDescription("N"));
		
		LabelPartition l = LabelPartition.fromDescriptionPaths(List.of(dp1, dp2, dp3));
		System.out.println(l);
		assertEquals("5 parts", 5, l.numberOfParts());
		assertEquals("Last part", List.of("O", "C"), l.getPart(4));
	}
	
}
