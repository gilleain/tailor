package tailor.datasource.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import tailor.datasource.xml.condition.ConditionXmlHandler;
import tailor.datasource.xml.condition.DistanceConditionXmlHandler;
import tailor.datasource.xml.condition.HBondConditionXmlHandler;
import tailor.datasource.xml.condition.TorsionConditionXmlHandler;
import tailor.datasource.xml.description.AtomDescriptionXmlHandler;
import tailor.datasource.xml.description.ChainDescriptionXmlHandler;
import tailor.datasource.xml.description.DescriptionXmlHandler;
import tailor.datasource.xml.description.GroupDescriptionXmlHandler;
import tailor.datasource.xml.description.ProteinDescriptionXmlHandler;
import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;


/**
 * @author maclean
 *
 */
public class XmlDescriptionReader {
    
    public class XmlMotifHandler extends DefaultHandler {
        
    	private ProteinDescription currentProteinDescription;
    	
        private ChainDescription currentChainDescription;
        
        private GroupDescription currentGroupDescription;
        
        private AtomDescription currentAtomDescription;	// TODO - don't really need this?
        
        private ChainDescription root;
        
        private Object currentParent;
        
        private Stack<Description> seenStack;
        
        private Map<String, DescriptionXmlHandler> descriptionHandlers;
        
        private PathXmlHandler pathXmlHandler;
        
        private Map<String, ConditionXmlHandler> conditionHandlers;
        
        public XmlMotifHandler() {
            this.seenStack = new Stack<>();
            
            descriptionHandlers = new HashMap<>();
            descriptionHandlers.put("ProteinDescription", new ProteinDescriptionXmlHandler());
            descriptionHandlers.put("ChainDescription", new ChainDescriptionXmlHandler());
            descriptionHandlers.put("GroupDescription", new GroupDescriptionXmlHandler());
            descriptionHandlers.put("AtomDescription", new AtomDescriptionXmlHandler());
            
            pathXmlHandler = new PathXmlHandler();
            
            conditionHandlers = new HashMap<>();
            conditionHandlers.put("DistanceCondition", new DistanceConditionXmlHandler());
            conditionHandlers.put("HBondCondition", new HBondConditionXmlHandler());
            conditionHandlers.put("TorsionCondition", new TorsionConditionXmlHandler());
        }
        
        @Override
        public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
            if (descriptionHandlers.containsKey(qName)) {
                DescriptionXmlHandler handler = descriptionHandlers.get(qName);
                try {
                	setCurrent(qName, handler, attrs);
//                    seenStack.push(currentDescription);
//                    currentParent = currentDescription;
                    
                    if (root == null) {
                        root = currentChainDescription;	// TODO?
                    }
                } catch (DescriptionParseException dpe) {
                    // TODO
                    System.err.println(dpe.toString());
                }
            } else if (conditionHandlers.containsKey(qName)) {
                conditionHandlers.get(qName).create(attrs);
            } else if (qName.equals("Path")) {
                pathXmlHandler.create(attrs, currentParent);
            }
        }
        
        private void setCurrent(String qName, DescriptionXmlHandler handler, Attributes attrs) throws DescriptionParseException {
        	// TODO - is there a cleaner way to do this?
        	if (handler instanceof ProteinDescriptionXmlHandler proteinDescriptionXmlHandler) {
        		currentProteinDescription = (ProteinDescription) proteinDescriptionXmlHandler.create(attrs, null);
        		currentParent = currentProteinDescription;
        	} else if (handler instanceof ChainDescriptionXmlHandler chainDescriptionXmlHandler) {
        		currentChainDescription = (ChainDescription) chainDescriptionXmlHandler.create(attrs, currentProteinDescription);
        		currentParent = currentChainDescription;
        	} else if (handler instanceof GroupDescriptionXmlHandler groupDescriptionXmlHandler) {
        		currentGroupDescription = (GroupDescription) groupDescriptionXmlHandler.create(attrs, currentChainDescription);
        		currentParent = currentGroupDescription;
        	} else if (handler instanceof AtomDescriptionXmlHandler atomDescriptionXmlHandler) {
        		currentAtomDescription = (AtomDescription) atomDescriptionXmlHandler.create(attrs, currentGroupDescription);
        	}
        }
        
        private void setCurrentParent(String qName, DescriptionXmlHandler handler) {
        	// TODO - is there a cleaner way to do this?
        	if (handler instanceof ProteinDescriptionXmlHandler) {
        		// end
        	} else if (handler instanceof ChainDescriptionXmlHandler) {
        		currentParent = currentProteinDescription;
        	} else if (handler instanceof GroupDescriptionXmlHandler) {
        		currentParent = currentChainDescription;
        	} else if (handler instanceof AtomDescriptionXmlHandler) {
        		currentParent = currentGroupDescription;
        	}
        }
        
        @Override
        public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
            if (descriptionHandlers.containsKey(qName)) {
            	setCurrentParent(qName, descriptionHandlers.get(qName));
            } else if (conditionHandlers.containsKey(qName)) {
            	// TODO - how do we check this
                conditionHandlers.get(qName).complete(currentChainDescription, pathXmlHandler);
            } 
        }
        
        public ChainDescription getDescription() {
            return this.root;
        }
        
    }
    
    public ChainDescription readDescription(File file) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            XmlMotifHandler handler = new XmlMotifHandler(); 
            saxParser.parse(file, handler);
            return handler.getDescription();
        } catch (SAXException s) {
            System.out.println(s);
            return null;
        } catch (IOException ioe) {
            System.out.println(ioe);
            return null;
        } catch (ParserConfigurationException pce) {
            System.out.println(pce);
            return null;
        }
    }
    
    public ChainDescription readDescription(InputStream stream) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            XmlMotifHandler handler = new XmlMotifHandler(); 
            saxParser.parse(new InputSource(stream), handler);
            return handler.getDescription();
        } catch (SAXException s) {
            System.out.println(s);
            return null;
        } catch (IOException ioe) {
            System.out.println(ioe);
            return null;
        } catch (ParserConfigurationException pce) {
            System.out.println(pce);
            return null;
        }
    }
}
