package tailor.match;

import tailor.description.Description;
import tailor.structure.Structure;

public interface MatchVisitor {
    
    public boolean visitMatch(Match match);
    
    public boolean visitDescription(Description description);
    
    public boolean visitStructure(Structure structure);

}
