package tailor.operator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static tailor.operator.TestSegmentPlanner.OperatorMatcher.operator;
import static tailor.structure.Segment.Type.HELIX;
import static tailor.structure.Segment.Type.STRAND;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import tailor.api.Operator;
import tailor.description.ChainDescription;
import tailor.description.segment.SegmentDescription;
import tailor.description.segment.SegmentDescriptionPath;
import tailor.description.segment.SegmentLengthDescription;
import tailor.engine.operator.FilterSegmentByPropertyDescription;
import tailor.engine.operator.SegmentCombiner;
import tailor.engine.operator.SegmentTypeFilter;
import tailor.engine.plan.Plan;
import tailor.engine.plan.SegmentPlanner;

public class TestSegmentPlanner {
	
	@Test
	public void testEHE() {
		ChainDescription chainDescription = new ChainDescription();
		chainDescription.addSegment(new SegmentDescription(STRAND));
		chainDescription.addSegment(new SegmentDescription(HELIX));
		chainDescription.addSegment(new SegmentDescription(STRAND));
		Plan plan = new SegmentPlanner().makePlan(chainDescription);
		assertThat(plan.getOperators(), 
				contains(
						operator(SegmentTypeFilter.class, "0"),
						operator(SegmentTypeFilter.class, "1"),
						operator(SegmentTypeFilter.class, "2"),
						operator(SegmentCombiner.class, "3"),
						operator(SegmentCombiner.class, "4")
				)
		);
	}
	
	@Test
	public void testSingleSegmentWithInnerCondition() {
		ChainDescription chainDescription = new ChainDescription();
		SegmentDescription strand = new SegmentDescription(STRAND);
		chainDescription.addSegment(strand);
		strand.addPropertyDescription(new SegmentLengthDescription(10, new SegmentDescriptionPath(chainDescription, strand)));
		
		Plan plan = new SegmentPlanner().makePlan(chainDescription);
		assertThat(plan.getOperators(), 
				contains(
						operator(SegmentTypeFilter.class, "0"),
						operator(FilterSegmentByPropertyDescription.class, "1")
				)
		);
	}
	
	static class OperatorMatcher extends TypeSafeMatcher<Operator> {
		
		private Class<? extends Operator> operatorClass;
		private String id;
		
		private OperatorMatcher(Class<? extends Operator> operatorClass, String id) {
			this.operatorClass = operatorClass;
			this.id = id;
		}
		
		public static Matcher<Operator> operator(Class<? extends Operator> operatorClass, String id) {
			return new OperatorMatcher(operatorClass, id);
		}
		
		@Override
		public void describeTo(org.hamcrest.Description description) {
			description.appendText("Expected class " + operatorClass + " with id " + id);
		}

		@Override
		protected boolean matchesSafely(Operator operator) {
			boolean typeMatches = operator.getClass() == operatorClass;
			return typeMatches && operator.getId().equals(id);
		}
		
	}

}
