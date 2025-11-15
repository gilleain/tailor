package tailor.datasource.aigen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import tailor.structure.Level;
import tailor.structure.Structure;

public class Visitor {
    private Level level;
    private BiFunction<Visitor, Structure, Boolean> action;
    private String data;
    private List<Visitor> children;
    private int index;
    public Structure store;

    public Visitor(Level level, BiFunction<Visitor, Structure, Boolean> action) {
        this(level, action, null);
    }

    public Visitor(Level level, BiFunction<Visitor, Structure, Boolean> action, String data) {
        this.level = level;
        this.action = action;
        this.data = data;
        this.children = new ArrayList<>();
        this.index = 0;
    }

    public void add(Visitor visitor) {
        this.children.add(visitor);
    }

    public boolean visit(Structure structure) {
    	// TODO - originally the idea seems to have been to match on properties of the
    	// structure (like data= 'GLY'??) a more general matching mechanism would be needed
//        if (this.data == null || this.data.equals(structure.getData())) {
    	if (this.data == null) {
            return this.action.apply(this, structure);
        } else {
            return false;
        }
    }

    public Level getLevel() {
        return level;
    }

    public List<Visitor> getChildren() {
        return children;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return this.level + " " + this.index;
    }
}
