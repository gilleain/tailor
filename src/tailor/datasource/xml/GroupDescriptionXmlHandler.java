package tailor.datasource.xml;

import org.xml.sax.Attributes;

import tailor.description.ChainDescription;
import tailor.description.GroupDescription;

public class GroupDescriptionXmlHandler {
    
    public GroupDescription create(Attributes attrs, ChainDescription chainDescription) {
        String labelStr = attrs.getValue("label");
        String nameStr = attrs.getValue("name");
        
        GroupDescription groupDescription;
        if (nameStr == null) {
            groupDescription = new GroupDescription();
        } else {
            groupDescription = new GroupDescription(nameStr);
        }
        
        if (labelStr != null) {
            groupDescription.setLabel(labelStr);
        }
        chainDescription.addGroupDescription(groupDescription);
        return groupDescription;
    }

}
