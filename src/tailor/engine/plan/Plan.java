package tailor.engine.plan;

import tailor.description.Description;

public class Plan {
    
    private PlanElement root;

    public void setRoot(PlanElement planElement) {
        this.root = planElement;
    }
    
    public PlanElement getRoot() {
        return this.root;
    }

    /**
     * Get the plan element corresponding to this description.
     * 
     * @param description
     * @return
     */
    public PlanElement getFor(Description description) {
        return find(description, root);
    }
    
    private PlanElement find(Description description, PlanElement current) {
        if (current.getDescription() != null && 
                current.getDescription().equals(description)) {
            return current;
        } else {
            for (PlanElement child : current.getChildren()) {
                PlanElement foundElement = find(description, child);
                if (foundElement != null) {
                    return foundElement;
                }
            }
        }
        return null;
    }

}
