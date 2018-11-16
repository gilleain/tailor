package tailor.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import tailor.structure.Chain;
import tailor.structure.SSE;
import tailor.structure.Strand;
import tailor.structure.Structure;

public class TestDsspReader {
    
    private static final String DIR = "data";
    
    @Test
    public void testSSERead() throws IOException {
        Structure structure = DsspReader.read(new File(DIR, "1tgx.dssp"));
        Chain chain = (Chain) structure;
        List<SSE> sses = chain.getSSEs(); 
        assertEquals(5, sses.size());
        
        int index = 0;
        assertTrue(isStrand(sses.get(index++), 2, 4));
        assertTrue(isStrand(sses.get(index++), 11, 13));
        assertTrue(isStrand(sses.get(index++), 20, 26));
        assertTrue(isStrand(sses.get(index++), 35, 39));
        assertTrue(isStrand(sses.get(index++), 49, 54));
    }
    
    public void p(SSE sse, int index) {
        System.out.println(sse.getClass().getSimpleName() + " " + index
                + " " + sse.getFirst().getId() 
                + "-" + sse.getLast().getId());
    }
    
    private boolean isStrand(SSE sse, int start, int end) {
        return sse instanceof Strand 
                && sse.getFirst().getId().equals(String.valueOf(start))
                && sse.getLast().getId().equals(String.valueOf(end));
    }

}
