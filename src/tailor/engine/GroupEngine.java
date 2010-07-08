package tailor.engine;

import tailor.datasource.Structure;
import tailor.description.AtomDescription;
import tailor.description.GroupDescription;

public class GroupEngine {  // TODO : implement the Engine interface

    public Match match(GroupDescription groupDescription, Structure group) {
        Match match = new Match(groupDescription, group);
        for (AtomDescription atomDescription : groupDescription) {
            String atomName = atomDescription.getName();
            System.out.println("getting atom " + atomName + " from " + group);
            Structure atom = group.getSubStructureByProperty("Name", atomName);
            if (atom == null) {
                return match;
            } else {
                match.associate(atomDescription, atom);
            }
        }
        return match;
    }
    
}
