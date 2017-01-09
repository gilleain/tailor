package tailor.datasource.xml;

import org.xml.sax.Attributes;

import tailor.description.ProteinDescription;

public class ProteinDescriptionXmlHandler {
    
    public ProteinDescription create(Attributes attrs) {
        return new ProteinDescription(attrs.getValue("name"));
    }

}
