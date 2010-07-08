package tailor.condition;

import tailor.description.Description;
import tailor.engine.Match;

/**
 * A condition is a measure with an associated constraint.
 *  
 * @author maclean
 *
 */
public interface Condition extends Cloneable {
	
	public boolean satisfiedBy(Match match);
	
	public boolean equals(Condition other);
	
	public Object clone();
	
	public boolean contains(Description d);
	
	public String toXml();

}
