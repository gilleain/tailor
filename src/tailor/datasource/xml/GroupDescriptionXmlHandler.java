package tailor.datasource.xml;

import org.xml.sax.Attributes;

import tailor.description.ChainDescription;
import tailor.description.GroupDescription;

public class GroupDescriptionXmlHandler {
    
    public GroupDescription create(Attributes attrs, ChainDescription chainDescription) {
        String positionStr = attrs.getValue("position");
        String nameStr = attrs.getValue("name");
        if (nameStr.equals("*")) {
            nameStr = null;
        }
        GroupDescription groupDescription = null;
        if (!positionStr.equals("")) {
            int position = Integer.parseInt(positionStr);
            // TODO : get rid of position string
            groupDescription = new GroupDescription(nameStr);
        }
        chainDescription.addGroupDescription(groupDescription);
        return groupDescription;
    }

}
