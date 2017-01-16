package tailor.datasource.xml;

import org.xml.sax.Attributes;

import tailor.description.Description;

/**
 * Creates a description from a set of attributes.
 * 
 * @author maclean
 *
 */
public interface DescriptionXmlHandler {
    
    public Description create(Attributes attrs, Description parent) throws DescriptionParseException;

}
