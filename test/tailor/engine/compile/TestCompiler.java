package tailor.engine.compile;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;
import tailor.engine.plan.Plan;

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
        assertEquals("Single plan element", 1, plan.getElements().size());
    }
}
