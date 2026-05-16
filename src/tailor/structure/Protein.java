package tailor.structure;

import java.util.ArrayList;
import java.util.List;

import tops.translation.model.PolymerType;

public class Protein {
    
    private final String name;
    
    private final List<Chain> chains;
    
    public Protein(String name) {
        this.name = name;
        this.chains = new ArrayList<>();
    }
    
    public List<Chain> chainsOfType(PolymerType chainType) {
        List<Chain> chainsToReturn = new ArrayList<>();
        for (Chain chain : chains) {
        	if (chain.getType().equals(chainType)) {
        		chainsToReturn.add(chain);
        	}
        }
        return chainsToReturn;
    }
   
    public void addChain(Chain chain) {
        this.chains.add(chain);
    }

    public String getName() {
        return name;
    }

    public List<Chain> getChains() {
        return this.chains;
    }
}
