package aigen.feature;

import java.util.ArrayList;
import java.util.List;

class Structure extends Feature {
 private String id;

 public Structure(String id) {
     super();
     this.id = id;
     this.levelCode = "S";
 }

 public List<Chain> chainsWithID(String chainID) {
     List<Chain> chains = new ArrayList<>();
     for (Feature model : this) {
         for (Feature chain : model) {
             if (chain instanceof Chain) {
                 Chain c = (Chain) chain;
                 if (chainID.isEmpty() || c.getChainID().equals(chainID)) {
                     chains.add(c);
                 }
             }
         }
     }
     return chains;
 }

 public List<Chain> chainsOfType(String chainType) {
     List<Chain> chainsToReturn = new ArrayList<>();
     for (Feature model : this) {
         for (Feature chain : model) {
             if (chain instanceof Chain) {
                 Chain c = (Chain) chain;
                 if (chainType.isEmpty() || c.getChainType().equals(chainType)) {
                     chainsToReturn.add(c);
                 }
             }
         }
     }
     return chainsToReturn;
 }

 @Override
 public String toString() {
     return id;
 }
}
