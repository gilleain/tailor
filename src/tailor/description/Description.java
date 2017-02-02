package tailor.description;

import java.util.List;

import tailor.condition.Condition;
import tailor.measurement.Measure;
import tailor.structure.Level;


/**
 * @author maclean
 *
 */
public interface Description extends Cloneable {
	
	public Object clone();
    
    public Description shallowCopy();
    
    public int size();
    
    public Level getLevel();
    
    public String getName();
    
    public void addSubDescription(Description subDescription);
    
    public void addCondition(Condition condition);
    
    public List<Condition> getConditions();
    
    public void addMeasure(Measure measure);
    
    public List<Measure> getMeasures();
    
    public List<? extends Description> getSubDescriptions();
    
    public Description getSubDescriptionAt(int i);
    
    public String toPathString();
    
    public String toXmlPathString();
    
    public Description getPathEnd();
    
    public boolean contains(Description d);
    
    public Description getByID(int id);
    
    public void setID(int id);
    
    public int getID();
    
}
