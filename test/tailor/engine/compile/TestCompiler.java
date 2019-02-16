package tailor.engine.compile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;
import tailor.engine.plan.Plan;
import tailor.engine.plan.PlanElement;

public class TestCompiler {

    @Test
    public void testScan() throws CompilerException {
        // a chain container, with no particular label
        Description chainDescription = new ChainDescription();
        
        // two groups, again with no particular details
        chainDescription.addSubDescription(new GroupDescription());
        chainDescription.addSubDescription(new GroupDescription());
        
        Compiler compiler = new Compiler();
        Plan plan = compiler.compile(chainDescription);
        PlanElement root = plan.getRoot();
        assertNotNull(root);
        dump(root, 0);
    }
    
    private void dump(PlanElement el, int indent) {
        String indentString = "";
        for (int i = 0; i < indent; i++) {
            indentString += "\t";
        }
        System.out.println(indentString + el.getLevel() + " " + el.getType());
        for (PlanElement child : el.getChildren()) {
            dump(child, indent + 1);
        }
    }
}
