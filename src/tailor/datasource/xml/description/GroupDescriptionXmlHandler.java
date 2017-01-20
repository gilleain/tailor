package tailor.datasource.xml.description;

import org.xml.sax.Attributes;

import tailor.datasource.xml.DescriptionParseException;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;

public class GroupDescriptionXmlHandler implements DescriptionXmlHandler {
    
    public GroupDescription create(Attributes attrs, Description parent) throws DescriptionParseException {
        if (parent instanceof ChainDescription) {
            ChainDescription chainDescription = (ChainDescription) parent;
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
        } else {
            throw new DescriptionParseException("Invalid parent "  + parent.getClass().getSimpleName());
        }
    }

}
