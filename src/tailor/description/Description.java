package tailor.description;

import java.util.ArrayList;

import tailor.Level;
import tailor.condition.Condition;
import tailor.datasource.Structure;
import tailor.geometry.Vector;


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
    
    public ArrayList<Condition> getConditions();
    
    public ArrayList<? extends Description> getSubDescriptions();
    
    public Vector findStructureCenter(Structure structure);
    
    public String toPathString();
    
    public String toXmlPathString();
    
    public Description getPathEnd();
    
    public boolean contains(Description d);
    
    
}
