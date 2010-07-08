package tailor.description;

import java.util.List;

import tailor.Level;
import tailor.condition.Condition;


/**
 * @author maclean
 *
 */
public interface Description extends Cloneable {
	
	public Object clone();
    
    public Description shallowCopy();
    
    public Level getLevel();
    
    public void addSubDescription(Description subDescription);
    
    public void addCondition(Condition condition);
    
    public List<Condition> getConditions();
    
    public List<? extends Description> getSubDescriptions();
    
    public String toPathString();
    
    public String toXmlPathString();
    
    public Description getPathEnd();
    
    public boolean contains(Description d);
    
    
}
