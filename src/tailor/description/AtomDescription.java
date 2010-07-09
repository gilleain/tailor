package tailor.description;

import java.util.ArrayList;
import java.util.List;

import tailor.Level;
import tailor.condition.Condition;
import tailor.datasource.Structure;
import tailor.geometry.Vector;
import tailor.measure.Measure;


/**
 * @author maclean
 *
 */
public class AtomDescription implements Description {
    
    private static final Level level = Level.ATOM;
    
    private String atomName;

    public AtomDescription() {
        this.atomName = null;
    }
    
    public AtomDescription(String name) {
        this.atomName = name;
    }
    
    public AtomDescription(AtomDescription atomDescription) {
    	this(atomDescription.atomName);
    }
    
    public boolean contains(Description d) {
    	return false;
    }
    
    public Object clone() {
    	return new AtomDescription(this);
    }
    
    public Description shallowCopy() {
        return new AtomDescription(this.atomName);
    }
    
    public int size() {
        return 1;
    }
    
    public Level getLevel() {
        return AtomDescription.level;
    }
    
    public void addCondition(Condition condition) {
        // TODO : Atoms can't really have conditions...
    }
    
    public ArrayList<Condition> getConditions() {
        // TODO : Atoms don't have conditions
        return new ArrayList<Condition>();
    }
    
    public void addMeasure(Measure measure) {
        // TODO
    }
    
    public List<Measure> getMeasures() {
        // TODO 
        return new ArrayList<Measure>();
    }
    
    public void addSubDescription(Description subDescription) {
        // TODO : this class should complain about this kind of abuse.
    }
    
    /*
     * TODO : consider removing this method and the null
     * constructor if never used...
     */
    public void setName(String atomName) {
        this.atomName = atomName;
    }
    
    public String getName() {
        return this.atomName;
    }
    
    public boolean nameMatches(String name) {
        return this.atomName.equals(name);
    }
    
    public boolean matches(Structure atom) {
        return this.atomName.equals(atom.getProperty("Name"));
    }
    
    public ArrayList<Description> getSubDescriptions() {
        return new ArrayList<Description>();
    }
    
    public Vector findStructureCenter(Structure atom) {
        return atom.getAtomCenter();
    }
    
    public Description getPathEnd() {
    	return this;
    }
    
    public String toXmlPathString() {
    	return "atom=\"" + this.atomName + "\"/>";
    }
    
    public String toPathString() {
        return this.atomName;
    }
    
    public String toString() {
        return "Atom " + this.atomName;
    }
}
