package tailor.engine.plan;

import java.util.ArrayList;
import java.util.List;

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
    
    public PlanElement(Type type, Level level) {
        this.type = type;
        this.level = level;
        this.children = new ArrayList<>();
    }
    
    public void addChild(PlanElement child) {
        this.children.add(child);
    }

}
