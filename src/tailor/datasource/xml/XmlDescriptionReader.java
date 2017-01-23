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
import tailor.description.Description;


/**
 * @author maclean
 *
 */
public class XmlDescriptionReader {
    
    public class XmlMotifHandler extends DefaultHandler {
        
        private Description currentDescription;
        
        private Description root;
        
        private Description currentParent;
        
        private Stack<Description> seenStack;
        
        private Map<String, DescriptionXmlHandler> descriptionHandlers;
        
        private PathXmlHandler pathXmlHandler;
        
        private Map<String, ConditionXmlHandler> conditionHandlers;
        
        public XmlMotifHandler() {
            this.seenStack = new Stack<Description>();
            
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
        
        public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
            if (descriptionHandlers.containsKey(qName)) {
                DescriptionXmlHandler handler = descriptionHandlers.get(qName);
                try {
                    currentDescription = handler.create(attrs, currentParent);
                    seenStack.push(currentDescription);
                    currentParent = currentDescription;
                    
                    if (root == null) {
                        root = currentDescription;
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
        
        public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
            if (descriptionHandlers.containsKey(qName)) {
                seenStack.pop();
                if (!seenStack.isEmpty()) {
                    currentParent = seenStack.peek();
                }
            } else if (conditionHandlers.containsKey(qName)) {
                conditionHandlers.get(qName).complete(currentParent, pathXmlHandler);
            } 
        }
        
        public Description getDescription() {
            return this.root;
        }
        
    }
    
    public Description readDescription(File file) {
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
    
    public Description readDescription(InputStream stream) {
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
