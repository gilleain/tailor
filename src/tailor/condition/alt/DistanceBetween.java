package tailor.condition.alt;

import tailor.description.Description;
import tailor.structure.Group;

public class DistanceBetween implements UniformCondition<Group> {
    
    private final Description firstGroupDescription;
    
    private final Description secondGroupDescription;
    
    public DistanceBetween(Description firstGroupDescription, Description secondGroupDescription) {
        this.firstGroupDescription = firstGroupDescription;
        this.secondGroupDescription = secondGroupDescription;
    }

    @Override
    public boolean allows(Group... groups) {
        // TODO Auto-generated method stub
        return false;
    }

}
