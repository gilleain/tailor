package tailor.datasource.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import tailor.condition.HBondCondition;
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
        
        private Map<String, String> dataStore;
        
        private Stack<Description> seenStack;
        
        private Map<String, DescriptionXmlHandler> descriptionHandlers;
        
        private PathXmlHandler pathXmlHandler;
        
        public XmlMotifHandler() {
            this.dataStore = new HashMap<String, String>();
            this.seenStack = new Stack<Description>();
            
            descriptionHandlers = new HashMap<>();
            descriptionHandlers.put("ProteinDescription", new ProteinDescriptionXmlHandler());
            descriptionHandlers.put("ChainDescription", new ChainDescriptionXmlHandler());
            descriptionHandlers.put("GroupDescription", new GroupDescriptionXmlHandler());
            descriptionHandlers.put("AtomDescription", new AtomDescriptionXmlHandler());
            
            pathXmlHandler = new PathXmlHandler();
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
            }
                
            if (qName.equals("HBondCondition")) {
                this.dataStore.put("haMax", attrs.getValue("haMax"));
                this.dataStore.put("dhaMin", attrs.getValue("dhaMin"));
                this.dataStore.put("haaMin", attrs.getValue("haaMin"));
                if (attrs.getIndex("isNegated") != -1) {
                    this.dataStore.put("isNegated", attrs.getValue("isNegated"));
                }
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
            }
            
            if (qName.equals("HBondCondition")) {
                
                // TODO : these data items might not exist / be complete
                double haMax = Double.parseDouble(this.dataStore.get("haMax"));
                double dhaMin = Double.parseDouble(this.dataStore.get("dhaMin"));
                double haaMin = Double.parseDouble(this.dataStore.get("haaMin"));
                List<Description> paths = pathXmlHandler.getPaths();
                
                // TODO : assumes order!
                Description d  = paths.get(0);
                Description h  = paths.get(1);
                Description a  = paths.get(2);
                Description aa = paths.get(3);
                HBondCondition hbond = new HBondCondition(d, h, a, aa, haMax, dhaMin, haaMin);
                
                if (this.dataStore.containsKey("isNegated")) {
                    boolean isNegated = Boolean.parseBoolean((String) this.dataStore.get("isNegated"));
                    System.err.println("setting negated to " + isNegated);
                    hbond.setNegated(isNegated);
                }
                
                this.currentParent.addCondition(hbond);
                
                // don't want to accumulate paths
                pathXmlHandler.clearPaths();
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
