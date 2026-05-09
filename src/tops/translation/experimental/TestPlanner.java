package tops.translation.experimental;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static tops.translation.experimental.TestPlanner.OperatorMatcher.operator;
import static tops.translation.model.BackboneSegment.Type.HELIX;
import static tops.translation.model.BackboneSegment.Type.STRAND;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

public class TestPlanner {
	
	@Test
	public void testA() {
		ChainDescription chainDescription = new ChainDescription();
		chainDescription.addSegment(new SegmentDescription(STRAND));
		chainDescription.addSegment(new SegmentDescription(HELIX));
		chainDescription.addSegment(new SegmentDescription(STRAND));
		Plan plan = new Planner().makePlan(chainDescription);
		assertThat(plan.getOperators(), 
				contains(
						operator(SegmentTypeFilter.class, "0"),
						operator(SegmentTypeFilter.class, "1"),
						operator(SegmentTypeFilter.class, "2"),
						operator(Combiner.class, "3"),
						operator(Combiner.class, "4")
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
