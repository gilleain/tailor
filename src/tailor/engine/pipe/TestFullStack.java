package tailor.engine.pipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tailor.datasource.PDBFileList;
import tailor.datasource.StructureSource;
import tailor.engine.execute.Filter;
import tailor.structure.Chain;
import tailor.structure.Protein;

/**
 * This test should move or go away when finer-grained tests in place.
 * 
 * @author gilleain
 *
 */
public class TestFullStack {
    
    @Test
    public void test1() throws IOException {
        String path = "";    // TODO
        StructureSource ss = new PDBFileList(path);
        
        StructureSourcePipe input = new StructureSourcePipe(ss);
        input.addChainFilterMapping(getFilter(), getOutput());
        input.run();
    }
    
    private Output<List<Chain>> getOutput() {
        return new Output<List<Chain>>() {
            
            @Override
            public void accept(List<Chain> chains) {
                chains.forEach(System.out::println);
            }
        };
    }
    
    private Filter<Chain, Protein> getFilter() {
        return new Filter<Chain, Protein>() {

            @Override
            public List<Chain> filter(Iterable<Protein> proteins) {
                List<Chain> chains = new ArrayList<>();
                Protein protein = proteins.iterator().next();
                for (Chain chain : protein.getChains()) {
                    if (chain.getProperty("type").equals("peptide")) {  // XXX TODO
                        chains.add(chain);
                    }
                }
                return chains;
            }
        };
    }

}
