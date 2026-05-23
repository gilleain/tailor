package tailor.structure;

import java.util.ArrayList;
import java.util.List;

public class Protein {

    private String id;

    private List<Chain> chains;

    public Protein() {
        this("");
    }

    public Protein(String id) {
        this.chains = new ArrayList<>();
        setID(id);
    }
    
    public void setID(String id) {
    	this.id = id.toLowerCase();
    }

    public String getID() {
        return this.id;
    }

    public void addChain(Chain chain) {
        this.chains.add(chain);
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


    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < this.chains.size(); i++) {
            Chain chain = (Chain) this.chains.get(i);
            stringBuffer.append(chain.toString());
        }
        return stringBuffer.toString();
    }

    public List<Chain> getChains() {
        return this.chains;
    }

}
