package tailor.datasource.xml.condition;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import tailor.condition.TorsionBoundCondition;
import tailor.datasource.xml.PathXmlHandler;
import tailor.description.Description;

public class TorsionConditionXmlHandler implements ConditionXmlHandler {
    
    private Map<String, String> dataStore;
    
    public TorsionConditionXmlHandler() {
        this.dataStore = new HashMap<String, String>();
    }

    @Override
    public void create(Attributes attrs) {
        this.dataStore.put("name", attrs.getValue("name"));
        this.dataStore.put("midPoint", attrs.getValue("midPoint"));
        this.dataStore.put("range", attrs.getValue("range"));
    }

    @Override
    public void complete(Description parent, PathXmlHandler pathXmlHandler) {
        String name = this.dataStore.get("name");
        double midPoint = Double.parseDouble(this.dataStore.get("midPoint"));
        double range = Double.parseDouble(this.dataStore.get("range"));
        
        Description pathA  = pathXmlHandler.getPath("a");
        Description pathB  = pathXmlHandler.getPath("b");
        Description pathC  = pathXmlHandler.getPath("c");
        Description pathD = pathXmlHandler.getPath("d");
        
        TorsionBoundCondition torsion = 
                new TorsionBoundCondition(name, pathA, pathB, pathC, pathD, midPoint, range);
        
        // add the condition to the parent description
        parent.addCondition(torsion);
        
        // don't want to accumulate paths
        pathXmlHandler.clearPaths();
    }

}
