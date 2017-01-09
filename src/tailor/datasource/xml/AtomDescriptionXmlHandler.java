package tailor.datasource.xml;

import org.xml.sax.Attributes;

import tailor.description.AtomDescription;
import tailor.description.GroupDescription;

public class AtomDescriptionXmlHandler {
    
    public AtomDescription create(Attributes attrs, GroupDescription groupDescription) {
        AtomDescription atomDescription = new AtomDescription(attrs.getValue("name"));
        groupDescription.addAtomDescription(atomDescription);
        return atomDescription;
    }

}
