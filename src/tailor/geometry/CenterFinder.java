package tailor.geometry;

import java.util.List;

import tailor.Level;
import tailor.datasource.Structure;
import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;
import tailor.engine.Match;

/**
 * Utility class that finds the center for a piece of structure, where the
 * center of an atom is just its coordinates, and the center of other 
 * hierarchical groups are ultimately averages of the atom centers. 
 * 
 * @author maclean
 *
 */
public class CenterFinder {
    
    public static Vector findCenter(int descriptionID, Match match) {
        Structure structure = match.getStructureByDescriptionID(descriptionID);
        if (structure.getLevel() == Level.ATOM) {
            return structure.getAtomCenter();
        }
        return null;
    }

    /**
     * Find the geometric center of the structure in match.
     * 
     * @param description
     * @param match
     * @return
     */
    public static Vector findCenter(Description description, Match match) {
        if (description.getLevel() != match.getLevel()) {
            return findCenter(description.getSubDescriptionAt(0), match);
        }
        Structure structure = match.getStructure();
        if (description.getLevel() == Level.ATOM 
                && match(description, structure)) {
            return structure.getAtomCenter(); 
        } else {
            Vector center = new Vector();
            int count = 0;
            List<? extends Description> subdescriptions = 
                description.getSubDescriptions();
            int size = subdescriptions.size();
//            System.out.println("trying all of " + subdescriptions); 
            for (int i = 0; i < size; i++) {
                Description subdescription = subdescriptions.get(i);
                for (int j = 0; j < match.size(); j++) {
                    Match submatch = match.getSubMatch(j);
                    if (match(subdescription, submatch.getStructure())) {
                        center.add(findCenter(subdescription, submatch));
                        count++;
                    }
                }
            }
            if (count == 0) {
                return center;
            } else {
                return center.divide(count);
            }
        }
    }
    
    public static boolean match(Description description, Structure structure) {
        if (description instanceof AtomDescription) {
            return matchAtom((AtomDescription)description, structure);
        } else if (description instanceof GroupDescription) {
            return matchGroup((GroupDescription)description, structure);
        } else if (description instanceof ChainDescription) {
            return matchChain((ChainDescription)description, structure);
        }
        return false;
    }
    
    public static boolean matchChain(ChainDescription description, Structure chain) {
//        System.out.println("matching " + description.getName() + " " + chain.getProperty("Name"));
        String name = description.getName();
        return name == null || chain.getProperty("Name").equals(name);
    }

    public static boolean matchGroup(GroupDescription description, Structure group) {
//        System.out.println("matching " + description.getName() + " " + group.getProperty("Name"));
        String name = description.getName();
        return name == null || group.getProperty("Name").equals(name);
    }
    
    public static boolean matchAtom(AtomDescription description, Structure atom) {
//        System.out.println("matching " + description.getName() + " " + atom.getProperty("Name"));
        String name = description.getName();
        return name == null || atom.getProperty("Name").equals(name);
    }
}
