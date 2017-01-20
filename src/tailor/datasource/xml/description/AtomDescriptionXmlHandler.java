package tailor.datasource.xml.description;

import org.xml.sax.Attributes;

import tailor.datasource.xml.DescriptionParseException;
import tailor.description.AtomDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;

public class AtomDescriptionXmlHandler implements DescriptionXmlHandler {
    
    public AtomDescription create(Attributes attrs, Description parent) throws DescriptionParseException {
        if (parent instanceof GroupDescription) {
            GroupDescription groupDescription = (GroupDescription) parent;
            AtomDescription atomDescription = new AtomDescription(attrs.getValue("name"));
            groupDescription.addAtomDescription(atomDescription);
            return atomDescription;
        } else {
            throw new DescriptionParseException("Invalid parent");
        }
    }

}
