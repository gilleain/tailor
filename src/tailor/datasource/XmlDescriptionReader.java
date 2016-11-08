package tailor.datasource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import tailor.condition.HBondCondition;
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
        
        private Description currentDescription;
        
        private ProteinDescription currentProtein;
        
        private ChainDescription currentChain;
        
        private GroupDescription currentGroup;
        
        private AtomDescription currentAtom;
        
        private Map<String, String> dataStore;
        
        private Map<String, List<Description>> pathMap;
        
        public XmlMotifHandler() {
            this.currentProtein = null;
            this.currentChain = null;
            this.currentGroup = null;
            this.currentAtom = null;
            this.dataStore = new HashMap<String, String>();
            this.pathMap = new HashMap<String, List<Description>>();
        }
        
        public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
            if (qName.equals("ProteinDescription")) {
                this.currentProtein = new ProteinDescription(attrs.getValue("name"));
                this.currentDescription = this.currentProtein;
            } else if (qName.equals("ChainDescription")) {
                this.currentChain = new ChainDescription(attrs.getValue("name"));
                this.currentProtein.addChainDescription(this.currentChain);
                this.currentDescription = this.currentChain;
            } else if (qName.equals("GroupDescription")) {
                String positionStr = attrs.getValue("position");
                String nameStr = attrs.getValue("name");
                if (nameStr.equals("*")) {
                    nameStr = null;
                }
                if (!positionStr.equals("")) {
                    int position = Integer.parseInt(positionStr);
                    // TODO : get rid of position string
                    this.currentGroup = new GroupDescription(nameStr);
                    this.currentChain.addGroupDescription(this.currentGroup);
                }
                this.currentDescription = this.currentGroup;
            } else if (qName.equals("AtomDescription")) {
                this.currentAtom = new AtomDescription(attrs.getValue("name"));
                this.currentGroup.addAtomDescription(this.currentAtom);
                this.currentDescription = this.currentAtom;
            } else if (qName.equals("HBondCondition")) {
                this.dataStore.put("haMax", attrs.getValue("haMax"));
                this.dataStore.put("dhaMin", attrs.getValue("dhaMin"));
                this.dataStore.put("haaMin", attrs.getValue("haaMin"));
                if (attrs.getIndex("isNegated") != -1) {
                    this.dataStore.put("isNegated", attrs.getValue("isNegated"));
                }
            } else if (qName.equals("Path")) {
                
                // create the path
                Description path = null;
                if (this.currentDescription == this.currentProtein) {
                    // TODO : handle paths at the structure level
                    //String chainName = attrs.getValue("chain");
                } else if (this.currentDescription == this.currentChain) {
                    String positionStr = attrs.getValue("position");
                    int position = Integer.parseInt(positionStr);
                    String atomName = attrs.getValue("atom");
                    path = this.currentChain.getPath(position, atomName);
                    
                } else if (this.currentDescription == this.currentGroup) {
                    // TODO : handle paths at the group level
                }
                
                // store the path
                System.err.println("adding path " + path.toPathString());
                if (this.dataStore.containsKey("paths")) {
                    List<Description> paths = this.pathMap.get("paths");
                    paths.add(path);
                } else {
                    List<Description> paths = new ArrayList<>();
                    paths.add(path);
                    this.pathMap.put("paths", paths);
                }
            }
        }
        
        public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
            if (qName.equals("HBondCondition")) {
                
                // TODO : these data items might not exist / be complete
                double haMax = Double.parseDouble((String) this.dataStore.get("haMax"));
                double dhaMin = Double.parseDouble((String) this.dataStore.get("dhaMin"));
                double haaMin = Double.parseDouble((String) this.dataStore.get("haaMin"));
                List<Description> paths = this.pathMap.get("paths");
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
                
                this.currentDescription.addCondition(hbond);
                
                // don't want to accumulate paths
                this.dataStore.remove("paths");
            } else if (qName.equals("GroupDescription")) {
                this.currentDescription = this.currentChain;
            }
            
        }
        
        public Description getDescription() {
            return (Description) this.currentProtein;
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
    
    
    public static void main(String[] args) {
        XmlDescriptionReader reader = new XmlDescriptionReader();
        Description d = reader.readDescription(new File(args[0]));
        System.out.println(d.toPathString());
    }

}
