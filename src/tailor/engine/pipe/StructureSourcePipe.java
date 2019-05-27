package tailor.engine.pipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tailor.datasource.StructureSource;
import tailor.engine.filter.Filter;
import tailor.structure.Chain;
import tailor.structure.Protein;
import tailor.structure.Structure;

public class StructureSourcePipe {
    
    private final StructureSource structureSource;   // TODO : use 'ProteinSource'?
    
    private final Map<Filter<Chain, Protein>, Output<List<Chain>>> chainFilterMap; 
    
    public StructureSourcePipe(StructureSource ss) {
        this.structureSource = ss;
        this.chainFilterMap = new HashMap<>();
    }
    
    public void addChainFilterMapping(Filter<Chain, Protein> filter, Output<List<Chain>> output) {
        this.chainFilterMap.put(filter, output);
    }
    
    public void run() throws IOException {
        while (structureSource.hasNext()) {
            Structure structure = structureSource.next();
            Protein protein = (Protein) structure;  // XXX
            List<Protein> proteins = new ArrayList<>();
            proteins.add(protein);  // XXX TODO ugh
            for (Entry<Filter<Chain, Protein>, Output<List<Chain>>> mapping : chainFilterMap.entrySet()) {
                Filter<Chain, Protein> filter = mapping.getKey();
                Output<List<Chain>> output = mapping.getValue();
                output.accept(filter.filter(proteins));
            }
        }
    }

}
