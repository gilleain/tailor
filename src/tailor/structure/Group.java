package tailor.structure;

import java.util.HashMap;
import java.util.Map;

public class Group implements Structure {
    
    private final Level level = Level.RESIDUE;
    
    private final Map<String, Atom> atomMap;
    
    public Group() {
        this.atomMap = new HashMap<String, Atom>();
    }
    
    public void addAtom(Atom atom) {
        this.atomMap.put(atom.getName(), atom);
    }

    @Override
    public void accept(StructureVisitor visitor) {
        visitor.visit(this);
        for (String atomName : atomMap.keySet()) {
            visitor.visit(atomMap.get(atomName));
        }
    }
    
    public void accept(HierarchyVisitor visitor) {
        visitor.enter(this);
        for (String atomName : atomMap.keySet()) {
            atomMap.get(atomName).accept(visitor);
        }
        visitor.exit(this);
    }

    @Override
    public Level getLevel() {
        return level;
    }

}
