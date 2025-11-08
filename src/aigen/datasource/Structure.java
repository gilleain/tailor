package aigen.datasource;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;


class Structure {
    private String label;
    private Constants level;
    private String data;
    private List<Structure> children;

    public Structure(String label, Constants level, String data) {
        this.label = label;
        this.level = level;
        this.data = data;
        this.children = new ArrayList<>();
    }

    public void add(Structure structure) {
        this.children.add(structure);
    }

    public void addAll(List<Structure> structures) {
        for (Structure structure : structures) {
            this.add(structure);
        }
    }

    public Structure copy() {
        return new Structure(this.label, this.level, this.data);
    }

    public boolean accept(Visitor visitor) {
        return visitor.visit(this);
    }

    public void passDown(Visitor visitor) {
        if (this.accept(visitor)) {
            for (Structure child : this.children) {
                child.passDown(visitor);
            }
        }
    }

    public List<Structure> select(Visitor visitor) {
        if (visitor.getLevel() == this.level) {
            List<List<Structure>> groups = this.scan(visitor);
            List<Structure> results = new ArrayList<>();
            for (List<Structure> group : groups) {
                Structure copySelf = this.copy();
                copySelf.addAll(group);
                results.add(copySelf);
            }
            return results;
        } else {
            List<Structure> selections = new ArrayList<>();
            for (Structure child : this.children) {
                List<Structure> results = child.select(visitor);
                for (Structure result : results) {
                    Structure copySelf = this.copy();
                    copySelf.add(result);
                    selections.add(copySelf);
                }
            }
            return selections;
        }
    }

    public List<List<Structure>> scan(Visitor visitor) {
        if (visitor.getChildren().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<List<Structure>> groups = new ArrayList<>();
            while (visitor.getIndex() <= this.children.size() - visitor.getChildren().size()) {
                List<Structure> group = this.findNextGroup(visitor);
                if (group != null) {
                    groups.add(group);
                }
            }
            visitor.setIndex(0);
            return groups;
        }
    }

    public List<Structure> findNextGroup(Visitor visitor) {
        List<Structure> group = new ArrayList<>();
        for (Visitor childVisitor : visitor.getChildren()) {
            Structure child = this.children.get(visitor.getIndex());
            visitor.setIndex(visitor.getIndex() + 1);
            if (child.accept(childVisitor)) {
                group.add(child.copy());
            } else {
                return null;
            }
        }
        return group;
    }

    public String getLabel() {
        return label;
    }

    public Constants getLevel() {
        return level;
    }

    public String getData() {
        return data;
    }

    public List<Structure> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return this.label + " " + this.data;
    }


    public static void main(String[] args) {
        Structure p = new Structure("Protein", Constants.PROTEIN, "AminoAcid");

        Structure cA = new Structure("ChainA", Constants.CHAIN, "A");
        Structure cB = new Structure("ChainB", Constants.CHAIN, "B");

        Structure r1 = new Structure("Residue1", Constants.RESIDUE, "TYR");
        Structure r2 = new Structure("Residue2", Constants.RESIDUE, "GLY");
        Structure r3 = new Structure("Residue3", Constants.RESIDUE, "GLY");
        Structure r4 = new Structure("Residue4", Constants.RESIDUE, "SER");

        Structure a1 = new Structure("Atom1", Constants.ATOM, "N");
        Structure a2 = new Structure("Atom2", Constants.ATOM, "C");
        Structure a3 = new Structure("Atom3", Constants.ATOM, "N");
        Structure a4 = new Structure("Atom4", Constants.ATOM, "C");
        Structure a5 = new Structure("Atom5", Constants.ATOM, "N");
        Structure a6 = new Structure("Atom6", Constants.ATOM, "C");
        Structure a7 = new Structure("Atom7", Constants.ATOM, "N");
        Structure a8 = new Structure("Atom8", Constants.ATOM, "C");

        p.add(cA);

        cA.add(r1);
        r1.add(a1);
        r1.add(a2);

        cA.add(r2);
        r2.add(a3);
        r2.add(a4);

        p.add(cB);

        cB.add(r3);
        r3.add(a5);
        r3.add(a6);

        cB.add(r4);
        r4.add(a6);
        r4.add(a7);

        Visitor printV = new Visitor(Constants.ANY, VisitorPattern::printAction);

        Visitor compositeV = new Visitor(Constants.CHAIN, VisitorPattern::levelEqual);
        compositeV.add(new Visitor(Constants.RESIDUE, VisitorPattern::levelEqual, "GLY"));
        
        List<Structure> selections = p.select(compositeV);
        for (Structure selection : selections) {
            selection.passDown(printV);
            System.out.println(selection);
            System.out.println("----------");
        }
    }
}
