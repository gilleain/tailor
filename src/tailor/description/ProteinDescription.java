package tailor.description;

import java.util.ArrayList;
import java.util.List;

import tailor.Level;
import tailor.condition.Condition;
import tailor.measure.Measure;


/**
 * @author maclean
 *
 */
public class ProteinDescription implements Description {
    
    private static final Level level = Level.PROTEIN;
    
    private String name;    // a name for the whole description
    
    private ArrayList<ChainDescription> chainDescriptions;
    
    private ArrayList<Condition> chainConditions;
    
    private List<Measure> chainMeasures;
    
    public ProteinDescription() {
        this("Motif");
    }
    
    public ProteinDescription(String name) {
        this.name = name;
        this.chainDescriptions = new ArrayList<ChainDescription>();
        this.chainConditions = new ArrayList<Condition>();
        this.chainMeasures = new ArrayList<Measure>();
    }
    
    public ProteinDescription(ProteinDescription description) {
    	this(description.getName());
    	for (ChainDescription chainDescription : description.getChainDescriptions()) {
    		this.chainDescriptions.add(new ChainDescription(chainDescription));
    	}
    	// TODO : chain conditions
    }
    
    public boolean hasChain(String name) {
    	for (ChainDescription chain : this.chainDescriptions) {
    		if (chain.hasName(name)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean contains(Description d) {
    	if (d.getLevel() == ProteinDescription.level) {
    		return this.equals(d);
    	} else {
    		for (ChainDescription chain : this.chainDescriptions) {
    			if (chain.contains(d)) {
    				return true;
    			}
    		}
    		return false;
    	}
    }
    
    public Object clone() {
    	return new ProteinDescription(this);
    }
    
    public Description shallowCopy() {
        return new ProteinDescription(this.name);
    }
    
    public Level getLevel() {
        return ProteinDescription.level;
    }
    
    public void addSubDescription(Description subDescription) {
        if (subDescription instanceof ChainDescription) {
            this.addChainDescription((ChainDescription) subDescription);
        } else {
            // TODO : type safety!
        }
    }
    
    public void addChainDescription(ChainDescription chainDescription) {
        this.chainDescriptions.add(chainDescription);
    }
    
    public void addCondition(Condition condition) {
        this.chainConditions.add(condition);
    }
    
    public void addChainCondition(Condition condition) {
        this.chainConditions.add(condition);
    }
    
    public void addMeasure(Measure measure) {
        chainMeasures.add(measure);
    }
    
    public ArrayList<Condition> getConditions() {
        return this.chainConditions;
    }
    
    public List<Measure> getMeasures() {
        return this.chainMeasures;
    }
    
    public ArrayList<ChainDescription> getSubDescriptions() {
        return this.chainDescriptions;
    }
    
    public ArrayList<ChainDescription> getChainDescriptions() {
        return this.chainDescriptions;
    }
    
    public ChainDescription getChainDescription(String chainName) {
    	for (ChainDescription chain : this.chainDescriptions) {
    		if (chain.hasName(chainName)) {
    			return chain;
    		}
    	}
    	return null;
    }
    
    public ProteinDescription getPath(String residueName, String atomName) {
    	return this.getPath(null, residueName, atomName);
    }
    
    public ProteinDescription getPath(int residuePosition, String atomName) {
    	return this.getPath(null, residuePosition, atomName);
    }
    
    public ProteinDescription getPath(String chainName, String residueName, String atomName) {
        
        // we don't really need to name paths, but I suppose it could be useful for debugging
        ProteinDescription root = new ProteinDescription(this.name);
        
        // XXX what happens if we have multiple chains?
        ChainDescription chainPath;
        if (chainName == null) {
            chainPath = this.chainDescriptions.get(0).getPath(residueName, atomName);
        } else {
        	chainPath = this.getChainDescription(chainName);
        }
        root.addChainDescription(chainPath);
        
        return root;
    }
    
    public ProteinDescription getPath(String chainName, int residuePosition, String atomName) {
        // we don't really need to name paths, but I suppose it could be useful for debugging
        ProteinDescription root = new ProteinDescription(this.name);
        
        // XXX what happens if we have multiple chains?
        ChainDescription chainPath;
        if (chainName == null) {
            chainPath = this.chainDescriptions.get(0).getPath(residuePosition, atomName);
        } else {
        	chainPath = this.getChainDescription(chainName).getPath(residuePosition, atomName);
        }
        root.addChainDescription(chainPath);
        
        return root;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public int size() {
        return this.chainDescriptions.size();
    }
    
    public Description getPathEnd() {
    	if (this.chainDescriptions.size() == 0) {
    		return this;
    	} else {
    		return this.chainDescriptions.get(0).getPathEnd();
    	}
    }
    
    public String toPathString() {
        StringBuffer s = new StringBuffer();
        s.append(this.name).append('/');
        for (ChainDescription chainDescription : this.chainDescriptions) {
            s.append(chainDescription.toPathString());
        }
        return s.toString();
    }
    
    public String toXmlPathString() {
    	// XXX we assume that there is only one Chain!
    	return this.chainDescriptions.get(0).toXmlPathString();
    }
    
    public String toString() {
        return "Motif : " + this.name;
    }

}
