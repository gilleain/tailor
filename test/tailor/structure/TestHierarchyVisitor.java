package tailor.structure;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TestHierarchyVisitor {
    
    public class PrintVisitor implements HierarchyVisitor {
        
        public StringBuffer stringBuffer = new StringBuffer(); 
        
        @Override
        public void enter(Structure structure) {
            Level level = structure.getLevel();
            
            switch (level) {
                case CHAIN: handleChain(structure); break;
                case RESIDUE: handleGroup(structure); break;
                default: break;
            }
            stringBuffer.append("[");
        }

        @Override
        public void exit(Structure structure) {
            stringBuffer.append("]");
        }

        @Override
        public void visit(Structure structure) {
            stringBuffer.append("A");
        }
        
        private void handleChain(Structure structure) {
            stringBuffer.append("C");
        }
        
        private void handleGroup(Structure structure) {
            stringBuffer.append("G");
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
        chain.accept(visitor);
        assertEquals("C[G[AA]]", visitor.stringBuffer.toString());
    }

}
