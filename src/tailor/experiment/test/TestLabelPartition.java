package tailor.experiment.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import tailor.experiment.condition.LabelPartition;
import tailor.experiment.description.AtomDescription;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.description.GroupDescription;

public class TestLabelPartition {
	
	@Test
	public void testFromTwoDescriptionPaths() {
		GroupDescription gd1 = new GroupDescription();
		gd1.setIndex(0);
		DescriptionPath dp1 = new DescriptionPath(gd1, new AtomDescription("O"));
		
		GroupDescription gd2 = new GroupDescription();
		gd2.setIndex(2);
		DescriptionPath dp2 = new DescriptionPath(gd2, new AtomDescription("C"));
		
		LabelPartition l = LabelPartition.fromDescriptionPaths(List.of(dp1, dp2));
		System.out.println(l);
		assertEquals("3 parts", 3, l.numberOfParts());
	}
	
	@Test
	public void testFromThreeDescriptionPaths() {
		GroupDescription gd1 = new GroupDescription();
		gd1.setIndex(0);
		DescriptionPath dp1 = new DescriptionPath(gd1, new AtomDescription("N"));
		
		GroupDescription gd2 = new GroupDescription();
		gd2.setIndex(2);
		DescriptionPath dp2 = new DescriptionPath(gd2, new AtomDescription("C"));
		
		GroupDescription gd3 = new GroupDescription();
		gd3.setIndex(5);
		DescriptionPath dp3 = new DescriptionPath(gd3, new AtomDescription("O"));
		
		LabelPartition l = LabelPartition.fromDescriptionPaths(List.of(dp1, dp2, dp3));
		System.out.println(l);
		assertEquals("6 parts", 6, l.numberOfParts());
	}
}
