package tailor.engine.plan;

import java.util.List;

import tailor.description.Description;

public class Plan {
    
    private List<PlanElement> elements; // TODO - don't need both this and root...
    
    private PlanElement root;

    public List<PlanElement> getElements() {
        return elements;
    }

    public void setRoot(PlanElement planElement) {
        this.root = planElement;
    }

    public PlanElement getFor(Description parent) {
        // TODO Auto-generated method stub
        return null;
    }

}
