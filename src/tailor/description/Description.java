package tailor.description;

import java.util.List;

import tailor.experiment.api.AtomListCondition;
import tailor.measurement.Measure;
import tailor.measurement.Measurement;
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
    
    public void addCondition(AtomListCondition condition);
    
    public List<AtomListCondition> getConditions();
    
    public void addMeasure(Measure<? extends Measurement> measure);
    
    public List<Measure<? extends Measurement>> getMeasures();
    
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
