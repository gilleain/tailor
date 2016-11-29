package tailor.structure;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;

public class TestAtomSelector {
    
    @Test
    public void singleGroupSingleAtom() {
        final String targetAtomName = "C";
        
        Group group = new Group();
        group.addAtom(new Atom("N"));
        group.addAtom(new Atom("C"));
        
        Description description = new GroupDescription();
        description.addSubDescription(new AtomDescription(targetAtomName));
        
        AtomSelector selector = new AtomSelector(description);
        group.accept(selector);
        List<Atom> selected = selector.get();
        assertEquals(1, selected.size());
        assertEquals(targetAtomName, selected.get(0).getName());
    }
    
    @Test
    public void multiGroupSingleAtom() {
        final String targetAtomName = "C";
        
        Group group1 = new Group();
        group1.addAtom(new Atom("N"));
        group1.addAtom(new Atom("O"));
        
        Group group2 = new Group();
        group1.addAtom(new Atom("N"));
        group1.addAtom(new Atom("C"));
        
        Chain chain = new Chain();
        chain.addGroup(group1);
        chain.addGroup(group2);
        
        Description description = new GroupDescription();
        description.addSubDescription(new AtomDescription(targetAtomName));
        
        AtomSelector selector = new AtomSelector(description);
        chain.accept(selector);
        List<Atom> selected = selector.get();
        assertEquals(1, selected.size());
        assertEquals(targetAtomName, selected.get(0).getName());
    }

}
