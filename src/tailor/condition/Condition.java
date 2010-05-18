package tailor.condition;

import tailor.datasource.Structure;
import tailor.description.Description;

public interface Condition extends Cloneable {
	
	public boolean satisfiedBy(Structure structure);
	
	public boolean equals(Condition other);
	
	public Object clone();
	
	public boolean contains(Description d);
	
	public String toXml();

}
