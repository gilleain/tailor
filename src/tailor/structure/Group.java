package tailor.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group implements Structure {
    
    private final Level level = Level.RESIDUE;
    
    private final Map<String, Atom> atomMap;

    private String id;
    
    private int index;
    
    private int number; // TODO - id, index, number...
    

    public Group() {
        this.atomMap = new HashMap<>();
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

    public String getId() {
        // TODO : why ID and name...
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String getName() {
        return id;
    }
    
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<Atom> getAtoms() {
        return new ArrayList<>(this.atomMap.values());
    }
    

    @Override
    public void addSubStructure(Structure structure) {
        if (structure instanceof Atom) {
            addAtom((Atom) structure);
        } else {
            throw new IllegalArgumentException("Can only add instances of " + Atom.class.getName());
        }
    }

    @Override
    public List<Structure> getSubstructures() {
        List<Structure> substructures = new ArrayList<>();
        substructures.addAll(getAtoms());
        return substructures;
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


}
