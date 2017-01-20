package tailor.datasource.xml.condition;

import org.xml.sax.Attributes;

import tailor.datasource.xml.PathXmlHandler;
import tailor.description.Description;

public interface ConditionXmlHandler {
    
    public void create(Attributes attrs);
    
    public void complete(Description parent, PathXmlHandler pathXmlHandler);

}
