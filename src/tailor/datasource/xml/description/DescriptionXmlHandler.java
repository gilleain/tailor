package tailor.datasource.xml.description;

import org.xml.sax.Attributes;

import tailor.datasource.xml.DescriptionParseException;

/**
 * Creates a description from a set of attributes.
 * 
 * @author maclean
 *
 */
public interface DescriptionXmlHandler {
    
    public Object create(Attributes attrs, Object parent) throws DescriptionParseException;

}
