package tailor.engine.filter;

import java.util.ArrayList;
import java.util.List;

import tailor.structure.Chain;
import tailor.structure.ChainType;
import tailor.structure.Protein;

public class ChainTypeFilter implements Filter<Chain, Protein> {
    
    private final ChainType chainType;
    
    public ChainTypeFilter(ChainType chainType) {
        this.chainType = chainType;
    }
    
    @Override
    public List<Chain> filter(Iterable<Protein> proteins) {
        List<Chain> chains = new ArrayList<>();
        Protein protein = proteins.iterator().next();
        for (Chain chain : protein.getChains()) {
            if (chain.getType() == chainType) {
                chains.add(chain);
            }
        }
        return chains;
    }

}
