package tailor.datasource.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;

public class PathXmlHandler {
    
    private List<Description> pathMap;
    
    public PathXmlHandler() {
        this.pathMap = new ArrayList<Description>();
    }
    
    public List<Description> getPaths() {
        return this.pathMap;
    }
    
    public void clearPaths() {
        this.pathMap.clear();
    }
    
    public void create(Attributes attrs, Description currentParent) {

        // create the path
        Description path = null;
        if (currentParent instanceof ProteinDescription) {
            // TODO : handle paths at the structure level
            //String chainName = attrs.getValue("chain");
        } else if (currentParent instanceof ChainDescription) {
            ChainDescription chainDescription = (ChainDescription) currentParent;
            String labelStr = attrs.getValue("label");
            String atomName = attrs.getValue("atom");
            path = chainDescription.getPathByGroupLabel(labelStr, atomName);
        } else if (currentParent instanceof GroupDescription) {
            // TODO : handle paths at the group level
        }
        
        // store the path
        System.err.println("adding path " + path.toPathString());
        pathMap.add(path);
    }

}
