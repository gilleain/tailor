package tailor.datasource.xml.description;

import org.xml.sax.Attributes;

import tailor.datasource.xml.DescriptionParseException;
import tailor.description.Description;
import tailor.description.ProteinDescription;

public class ProteinDescriptionXmlHandler implements DescriptionXmlHandler {
    
    @Override
    public Description create(Attributes attrs, Description parent) throws DescriptionParseException {
        // XXX do we care about the parent?
        return new ProteinDescription(attrs.getValue("name"));
    }

}
