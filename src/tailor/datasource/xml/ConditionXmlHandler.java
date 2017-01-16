package tailor.datasource.xml;

import org.xml.sax.Attributes;

import tailor.description.Description;

public interface ConditionXmlHandler {
    
    public void create(Attributes attrs);
    
    public void complete(Description parent, PathXmlHandler pathXmlHandler);

}
