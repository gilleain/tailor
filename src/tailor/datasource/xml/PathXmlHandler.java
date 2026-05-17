package tailor.datasource.xml;

import static tailor.description.GroupDescriptionPath.getPathByLabel;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import tailor.description.ChainDescription;
import tailor.description.GroupDescriptionPath;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;

public class PathXmlHandler {
    
    private Map<String, GroupDescriptionPath> pathMap;
    
    public PathXmlHandler() {
        this.pathMap = new HashMap<>();
    }
    
    public GroupDescriptionPath getPath(String name) {
        return this.pathMap.get(name);
    }
    
    public void clearPaths() {
        this.pathMap.clear();
    }
    
    public void create(Attributes attrs, Object currentParent) {

        // create the path
        GroupDescriptionPath path = null;
        String name = null;
        
        if (currentParent instanceof ProteinDescription) {
            // TODO : handle paths at the structure level
            //String chainName = attrs.getValue("chain");
        } else if (currentParent instanceof ChainDescription) {
            ChainDescription chainDescription = (ChainDescription) currentParent;
            name = attrs.getValue("name");
            String labelStr = attrs.getValue("label");
            String atomName = attrs.getValue("atom");
            path = getPathByLabel(chainDescription, labelStr, atomName);
        } else if (currentParent instanceof GroupDescription) {
            // TODO : handle paths at the group level
        }
        
        // store the path
        if (path == null) {
            System.err.println("adding path " + name + " null");
        } else {
            System.err.println("adding path " + name + " " + path);
        }
        pathMap.put(name, path);
    }
    
  

}
