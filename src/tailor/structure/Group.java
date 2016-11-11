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
    public void visit(StructureVisitor visitor) {
        visitor.visit(this);
        for (String atomName : atomMap.keySet()) {
            visitor.visit(atomMap.get(atomName));
        }
    }

    @Override
    public Level getLevel() {
        return level;
    }

}
