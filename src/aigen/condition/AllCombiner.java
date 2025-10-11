package aigen.condition;

import java.util.ArrayList;
import java.util.List;

class AllCombiner extends BaseCondition {
	 private List<Condition> conditions;
	 
	 public AllCombiner() {
	     this.conditions = new ArrayList<>();
	 }
	 
	 public AllCombiner(List<Condition> conditions) {
	     this.conditions = conditions != null ? conditions : new ArrayList<>();
	 }
	 
	 @Override
	 public boolean satisfiedBy(Object feature) {
	     for (Condition condition : conditions) {
	         if (!condition.satisfiedBy(feature)) {
	             return false;
	         }
	     }
	     return true;
	 }
	 
	 @Override
	 public String toString() {
	     StringBuilder sb = new StringBuilder("All of [");
	     for (int i = 0; i < conditions.size(); i++) {
	         if (i > 0) sb.append(", ");
	         sb.append(conditions.get(i).toString());
	     }
	     sb.append("]");
	     return sb.toString();
	 }
	}
