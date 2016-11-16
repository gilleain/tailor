package tailor.geometry;

import tailor.structure.Atom;
import tailor.structure.Level;
import tailor.structure.Structure;
import tailor.structure.StructureVisitor;

public class CenterVisitor implements StructureVisitor {
    
    private Vector center;
    
    private int counter;
    
    public CenterVisitor() {
        center = new Vector();
    }
    
    public Vector get() {
        return center.divide(Double.valueOf(counter));
    }

    @Override
    public void visit(Structure structure) {
        Level level = structure.getLevel();
        
        switch (level) {
            case CHAIN: handleChain(structure); break;
            case RESIDUE: handleGroup(structure); break;
            case ATOM: handleAtom(structure); break;            
            default: break;
        }
    }
    
    private void handleChain(Structure structure) {
    }
    
    private void handleGroup(Structure structure) {
    }
    
    private void handleAtom(Structure structure) {
        Atom atom = (Atom)structure;
        center = center.plus(atom.getCenter());
        counter++;
    }

}
