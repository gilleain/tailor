package tailor.engine.compile;

import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;
import tailor.engine.plan.Plan;
import tailor.engine.plan.PlanElement;
import tailor.engine.plan.PlanElement.Type;
import tailor.structure.Level;

/**
 * Compiles descriptions (patterns) into plans that can be executed.
 * 
 * @author gilleain
 *
 */
public class Compiler {
    
    public Plan compile(Description description) throws CompilerException {
        Plan plan = new Plan();
        convert(null, description, plan);
        return plan;
    }
    
    private void convert(Description parent, Description description, Plan plan) throws CompilerException {
        switch (description.getLevel()) {
            case ATOM:
                handleAtomDescription(parent, (AtomDescription) description, plan);
                break;
            case CHAIN:
                handleChainDescription(parent, (ChainDescription) description, plan);
                break;
            case PROTEIN:
                break;
            case RESIDUE:
                handleGroupDescription(parent, (GroupDescription) description, plan);
                break;
            case SSE:
                break;
            case UNKNOWN:
                break;
            default:
                break;
            
        }
        for (Description childDescription : description.getSubDescriptions()) {
            convert(description, childDescription, plan);
        }
    }
    
    private void handleChainDescription(Description parent, ChainDescription description, Plan plan) {
        PlanElement parentElement;
        if (parent == null) {
            parentElement = new PlanElement(PlanElement.Type.SELECTOR, Level.PROTEIN);
            plan.setRoot(parentElement);
        } else {
            parentElement = plan.getFor(parent);
        }
        
        PlanElement childElement = new PlanElement(Type.SCANNER, Level.CHAIN);
        parentElement.addChild(childElement);
    }
    
    private void handleGroupDescription(Description parent, GroupDescription groupDescription, Plan plan) {
        PlanElement parentElement;
        if (parent == null) {
            parentElement = new PlanElement(PlanElement.Type.SCANNER, Level.CHAIN);
            plan.setRoot(parentElement);
        } else {
            parentElement = plan.getFor(parent);
        }
        
        PlanElement childElement = new PlanElement(Type.SCANNER, Level.RESIDUE);
        parentElement.addChild(childElement);
    }
    
    private void handleAtomDescription(Description parent, AtomDescription atomDescription, Plan plan) throws CompilerException {
        if (parent == null) {
            throw new CompilerException("Atoms must have a parent group");
        }
    }

}
