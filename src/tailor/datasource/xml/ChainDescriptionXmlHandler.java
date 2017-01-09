package tailor.datasource.xml;

import org.xml.sax.Attributes;

import tailor.description.ChainDescription;
import tailor.description.ProteinDescription;

public class ChainDescriptionXmlHandler {
    
    public ChainDescription create(Attributes attrs, ProteinDescription proteinDescription) {
        ChainDescription chainDescription = new ChainDescription(attrs.getValue("name"));
        proteinDescription.addChainDescription(chainDescription);
        return chainDescription;
    }

}
