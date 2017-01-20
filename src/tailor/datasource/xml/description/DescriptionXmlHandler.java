package tailor.datasource.xml.description;

import org.xml.sax.Attributes;

import tailor.datasource.xml.DescriptionParseException;
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
