package tailor.structure;

public class Atom implements Structure {
    
    private final Level level = Level.ATOM;
    
    private final String name;
    
    public Atom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(StructureVisitor visitor) {
        visitor.visit(this);
    }
    
    public void accept(HierarchyVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Level getLevel() {
        return level;
    }

}