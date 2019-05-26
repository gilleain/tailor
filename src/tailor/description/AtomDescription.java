package tailor.description;

import java.util.ArrayList;
import java.util.List;

import tailor.condition.Condition;
import tailor.condition.PropertyCondition;
import tailor.measurement.Measure;
import tailor.measurement.Measurement;
import tailor.structure.Atom;
import tailor.structure.Level;
import tailor.structure.Structure;


/**
 * @author maclean
 *
 */
public class AtomDescription implements Description {
    
    private static final Level level = Level.ATOM;
    
    private List<Condition> conditions;
    
    private int id;
    
    public AtomDescription() {
        this.conditions = new ArrayList<>();
    }
    
    public AtomDescription(String name) {
        this();
        // TODO : resolve!
        addCondition(new PropertyCondition("Name", name));
    }
    
    public AtomDescription(AtomDescription atomDescription) {
    	this();
    	this.setName(atomDescription.getName());
    }
    
    /*
     * TODO : consider removing this method and the null
     * constructor if never used...
     */
    public void setName(String atomName) {
        addCondition(new PropertyCondition("Name", atomName));
    }

    public boolean nameMatches(String name) {
        return this.getName().equals(name);
    }

    public boolean matches(Structure atom) {
        return atom instanceof Atom &&
                this.getName().equals(((Atom)atom).getName());
    }
    
    public int getID() {
        return this.id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public Description getByID(int id) {
        if (id == this.id) {
            return this;
        } else {
            return null;
        }
    }
    
    public boolean contains(Description d) {
    	return false;
    }
    
    public Object clone() {
    	return new AtomDescription(this.getName());
    }
    
    public Description shallowCopy() {
        return new AtomDescription(this.getName());
    }
    
    public int size() {
        return 1;
    }
    
    public Level getLevel() {
        return AtomDescription.level;
    }
    
    public void addCondition(Condition condition) {
        this.conditions.add(condition);
    }
    
    public List<Condition> getConditions() {
        return this.conditions;
    }
    
    public void addMeasure(Measure<? extends Measurement> measure) {
        // TODO
    }
    
    public List<Measure<? extends Measurement>> getMeasures() {
        // TODO 
        return new ArrayList<>();
    }
    
    public void addSubDescription(Description subDescription) {
        // TODO : this class should complain about this kind of abuse.
    }
    
    public String getName() {
        String name = "";
        for (Condition condition : conditions) {
            if (condition instanceof PropertyCondition) {
                PropertyCondition prop = (PropertyCondition) condition;
                if (prop.keyEquals("Name")) {
                    return prop.getValue();
                }
            }
        }
        return name;
    }
    
    public List<Description> getSubDescriptions() {
        return new ArrayList<>();
    }
    
    public Description getSubDescriptionAt(int i) {
        // throw error?
        return null;
    }
    
    public Description getPathEnd() {
    	return this;
    }
    
    public String toXmlPathString() {
    	return "atom=\"" + this.getName() + "\"/>";
    }
    
    public String toPathString() {
        return this.getName();
    }
    
    public String toString() {
        return "Atom " + this.getName();
    }
}
