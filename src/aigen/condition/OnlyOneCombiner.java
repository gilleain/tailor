package aigen.condition;

import java.util.ArrayList;
import java.util.List;

class OnlyOneCombiner extends BaseCondition {
	 private List<Condition> conditions;
	 
	 public OnlyOneCombiner() {
	     this.conditions = new ArrayList<>();
	 }
	 
	 public OnlyOneCombiner(List<Condition> conditions) {
	     this.conditions = conditions != null ? conditions : new ArrayList<>();
	 }
	 
	 @Override
	 public boolean satisfiedBy(Object feature) {
	     boolean satisfied = false;
	     for (Condition condition : conditions) {
	         if (condition.satisfiedBy(feature)) {
	             if (satisfied) {
	                 return false; // More than one satisfied
	             }
	             satisfied = true;
	         }
	     }
	     return satisfied;
	 }
	 
	 @Override
	 public String toString() {
	     StringBuilder sb = new StringBuilder("One of [");
	     for (int i = 0; i < conditions.size(); i++) {
	         if (i > 0) sb.append(", ");
	         sb.append(conditions.get(i).toString());
	     }
	     sb.append("]");
	     return sb.toString();
	 }
	}
