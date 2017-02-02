package tailor.description;

import org.junit.Test;

import tailor.condition.DistanceBoundCondition;

public class DescriptionFactoryTests {
    
    public void setupFactory(DescriptionFactory factory) {
        factory.addChainToProtein("A");
        
        factory.addResidueToChain("A", "GLY");
        factory.addResidueToChain("A", "LYS");
        
        factory.addAtomToResidue("A", 0, "N");
        factory.addAtomToResidue("A", 1, "O");
    }
    
    @Test
    public void makeProduct() {
        DescriptionFactory factory = new DescriptionFactory("Motif");
        setupFactory(factory);
        
        ProteinDescription description = factory.getProduct();
        ProteinDescription a = description.getPath("A", "GLY", "N");
        ProteinDescription b = description.getPath("A", "LYS", "O");
        
        System.out.println(a.toPathString());
        System.out.println(b.toPathString());
    }
    
    @Test
    public void makeBasicCondition() {
        DescriptionFactory factory = new DescriptionFactory("Motif");
        setupFactory(factory);
        
        ProteinDescription description = factory.getProduct();
        
        ChainDescription chain = description.getChainDescription("A");
        ChainDescription a = chain.getPathByGroupName("GLY", "N");
        ChainDescription b = chain.getPathByGroupName("LYS", "O");
        
        DistanceBoundCondition bound = new DistanceBoundCondition("NODistance", a, b, 4, 2);
        chain.addGroupCondition(bound);
        // TODO - asserts!
    }

}
