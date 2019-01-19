package tailor.structure;

import java.util.List;

import tailor.geometry.Vector;

public class Atom implements Structure {
    
    private final Level level = Level.ATOM;
    
    private final String name;
    
    private final Vector center;
    
    public Atom(String name) {
        this.name = name;
        this.center = new Vector();
    }
    
    public Atom(String name, Vector center) {
        this.name = name;
        this.center = center;
    }

    public Vector getCenter() {
        return center;
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

    @Override
    public List<Structure> getSubstructures() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProperty(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void copyProperty(Structure other, String key) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean hasPropertyEqualTo(String string, String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setProperty(String string, String string2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addSubStructure(Structure structure) {
        // can never add any substructure to an atom
        throw new UnsupportedOperationException();
    }
}
