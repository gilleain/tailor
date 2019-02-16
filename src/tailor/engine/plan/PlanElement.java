package tailor.engine.plan;

import java.util.ArrayList;
import java.util.List;

import tailor.description.Description;
import tailor.structure.Level;

public class PlanElement {
    
    public enum Type {
        SCANNER,     // linear scan through a sequence
        COMBINER,    // combinatorial selector
        SELECTOR     // property-based selector
    }
    
    private final Type type;
    
    private final Level level;
    
    private final List<PlanElement> children;
    
    /**
     * The original part of the description that generated this plan element.
     */
    private final Description description;
    
    public PlanElement(Description description, Type type, Level level) {
        this.description = description;
        this.type = type;
        this.level = level;
        this.children = new ArrayList<>();
    }
    
    public void addChild(PlanElement child) {
        this.children.add(child);
    }
    
    public List<PlanElement> getChildren() {
        return this.children;
    }
    
    public Description getDescription() {
        return this.description;
    }

    public Type getType() {
        return type;
    }

    public Level getLevel() {
        return level;
    }
    
}
