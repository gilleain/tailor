package aigen.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class Visitor {
    private Constants level;
    private BiFunction<Visitor, Structure, Boolean> action;
    private String data;
    private List<Visitor> children;
    private int index;
    public Structure store;

    public Visitor(Constants level, BiFunction<Visitor, Structure, Boolean> action) {
        this(level, action, null);
    }

    public Visitor(Constants level, BiFunction<Visitor, Structure, Boolean> action, String data) {
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
        if (this.data == null || this.data.equals(structure.getData())) {
            return this.action.apply(this, structure);
        } else {
            return false;
        }
    }

    public Constants getLevel() {
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
