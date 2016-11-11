package tailor.structure;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;

public class TestVisitor {
    
    public class PrintVisitor implements StructureVisitor {
        
        public StringBuffer stringBuffer = new StringBuffer(); 

        @Override
        public void visit(Structure structure) {
            // this could equally be done with getClass, but slightly simpler
            Level level = structure.getLevel();
            
            switch (level) {
                case CHAIN: handleChain(structure); break;
                case RESIDUE: handleGroup(structure); break;
                case ATOM: handleAtom(structure); break;            
                default: break;
            }
        }
        
        private void handleChain(Structure structure) {
            stringBuffer.append("C");
        }
        
        private void handleGroup(Structure structure) {
            stringBuffer.append("G");
        }
        
        private void handleAtom(Structure structure) {
            stringBuffer.append("A");
        }
    }
    
    @Test
    public void testPrint() {
        Atom aN = new Atom("N");
        Atom aO = new Atom("O");
        
        Group group = new Group();
        group.addAtom(aN);
        group.addAtom(aO);
        
        Chain chain = new Chain();
        chain.addGroup(group);
        
        PrintVisitor visitor = new PrintVisitor(); 
        chain.visit(visitor);
        assertEquals("CGAA", visitor.stringBuffer.toString());
    }

}
