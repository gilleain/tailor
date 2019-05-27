package tailor.engine.pipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tailor.datasource.PDBFileList;
import tailor.datasource.StructureSource;
import tailor.engine.filter.ChainTypeFilter;
import tailor.engine.filter.Filter;
import tailor.structure.Chain;
import tailor.structure.ChainType;
import tailor.structure.Protein;

/**
 * This test should move or go away when finer-grained tests in place.
 * 
 * @author gilleain
 *
 */
public class TestFullStack {
    
    private static final String DIR = "data";
    
    @Test
    public void test1() throws IOException {
        File file = new File(DIR, "test.pdb");
        StructureSource ss = new PDBFileList(file);
        
        StructureSourcePipe input = new StructureSourcePipe(ss);
        input.addChainFilterMapping(new ChainTypeFilter(ChainType.PEPTIDE), getOutput());
        input.run();
    }
    
    private Output<List<Chain>> getOutput() {
        return new Output<List<Chain>>() {
            
            @Override
            public void accept(List<Chain> chains) {
                chains.forEach(c -> System.out.println(c.getName()));
            }
        };
    }

}
