//package tailor.engine;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//
//import org.junit.Test;
//
//import tailor.datasource.PDBFileList;
//import tailor.datasource.StructureSource;
//import tailor.engine.filter.ChainTypeFilter;
//import tailor.engine.pipe.Output;
//import tailor.engine.pipe.StructureSourcePipe;
//import tailor.structure.Chain;
//import tailor.structure.ChainType;
//
///**
// * This test should move or go away when finer-grained tests in place.
// * 
// * @author gilleain
// *
// */
//public class TestFullStack {
//    
//    private static final String DIR = "data";
//    
//    @Test
//    public void test1() throws IOException {
//        File file = new File(DIR, "test.pdb");
//        StructureSource ss = new PDBFileList(file);
//        
//        StructureSourcePipe input = new StructureSourcePipe(ss);
//        input.addChainFilterMapping(new ChainTypeFilter(ChainType.PEPTIDE), getOutput());
//        input.run();
//    }
//    
//    private Output<List<Chain>> getOutput() {
//        return new Output<List<Chain>>() {
//            
//            @Override
//            public void accept(List<Chain> chains) {
//                chains.forEach(c -> System.out.println(c.getName()));
//            }
//        };
//    }
//
//}
