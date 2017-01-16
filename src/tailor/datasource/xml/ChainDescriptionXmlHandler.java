package tailor.datasource.xml;

import org.xml.sax.Attributes;

import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.ProteinDescription;

public class ChainDescriptionXmlHandler implements DescriptionXmlHandler {
    
    @Override
    public Description create(Attributes attrs, Description parent) throws DescriptionParseException {
        if (parent instanceof ProteinDescription) {
            ProteinDescription proteinDescription = (ProteinDescription) parent; 
            ChainDescription chainDescription = new ChainDescription(attrs.getValue("name"));
            proteinDescription.addChainDescription(chainDescription);
            return chainDescription;
        } else {
            throw new DescriptionParseException("Invalid parent " + parent.getClass().getSimpleName());
        }
    }

}
