package tailor.match;

import tailor.description.Description;
import tailor.structure.Structure;

public class AtomSelector implements MatchVisitor {

    @Override
    public boolean visitMatch(Match match) {
        return match.accept(this);
    }

    @Override
    public boolean visitDescription(Description description) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean visitStructure(Structure structure) {
        // TODO Auto-generated method stub
        return false;
    }

}
