package tailor.datasource.xml.condition;

import org.xml.sax.Attributes;

import tailor.datasource.xml.PathXmlHandler;
import tailor.description.ChainDescription;

public interface ConditionXmlHandler {
    
    public void create(Attributes attrs);
    
    public void complete(ChainDescription parent, PathXmlHandler pathXmlHandler);

}
