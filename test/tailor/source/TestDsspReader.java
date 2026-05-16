package tailor.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import tailor.datasource.DsspReader;
import tailor.structure.Chain;
import tailor.structure.Segment;

public class TestDsspReader {
    
    private static final String DIR = "data";
    
    @Test
    public void testSSERead() throws IOException {
        Chain chain = DsspReader.read(new File(DIR, "1tgx.dssp"));
        List<Segment> sses = chain.getSegments();
        assertEquals(5, sses.size());
        
        int index = 0;
        assertTrue(isStrand(sses.get(index++), 2, 4));
        assertTrue(isStrand(sses.get(index++), 11, 13));
        assertTrue(isStrand(sses.get(index++), 20, 26));
        assertTrue(isStrand(sses.get(index++), 35, 39));
        assertTrue(isStrand(sses.get(index++), 49, 54));
    }
    
    public void p(Segment sse, int index) {
        System.out.println(sse.getClass().getSimpleName() + " " + index
                + " " + sse.firstResidue().getNumber() 
                + "-" + sse.lastResidue().getNumber());
    }
    
    private boolean isStrand(Segment sse, int start, int end) {
        return sse.getType() == tailor.structure.Segment.Type.STRAND
                && sse.firstResidue().getNumber() == start
                && sse.lastResidue().getNumber() == end;
    }

}
