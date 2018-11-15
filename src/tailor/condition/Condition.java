package tailor.condition;

import tailor.description.Description;
import tailor.match.Match;

/**
 * A condition is a measure with an associated constraint.
 *  
 * @author maclean
 *
 */
public interface Condition {
	
	public boolean satisfiedBy(Match match);
	
	public boolean contains(Description d);
	
	public String toXml();

}
