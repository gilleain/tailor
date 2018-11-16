package tailor.structure;

import java.util.ArrayList;
import java.util.List;

public class Protein implements Structure {
    
    private final Level level = Level.PROTEIN;
    
    private final String name;
    
    private final List<Chain> chains;
    
    public Protein(String name) {
        this.name = name;
        this.chains = new ArrayList<>();
    }
   
    public void addChain(Chain chain) {
        this.chains.add(chain);
    }

    @Override
    public void accept(StructureVisitor visitor) {
        visitor.visit(this);
        for (Chain chain : chains) {
            chain.accept(visitor);
        }
    }
    
    @Override
    public void accept(HierarchyVisitor visitor) {
        visitor.enter(this);
        for (Chain chain : chains) {
            chain.accept(visitor);
        }
        visitor.exit(this);
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<Chain> getChains() {
        return this.chains;
    }

}
