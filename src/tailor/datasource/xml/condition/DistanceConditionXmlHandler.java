package tailor.datasource.xml.condition;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import tailor.condition.DistanceBoundCondition;
import tailor.datasource.xml.PathXmlHandler;
import tailor.description.Description;

public class DistanceConditionXmlHandler implements ConditionXmlHandler {
    
    private Map<String, String> dataStore;
    
    public DistanceConditionXmlHandler() {
        this.dataStore = new HashMap<>();
    }

    @Override
    public void create(Attributes attrs) {
        this.dataStore.put("center", attrs.getValue("center"));
        this.dataStore.put("range", attrs.getValue("range"));
    }

    @Override
    public void complete(Description currentParent, PathXmlHandler pathXmlHandler) {
        // TODO : these data items might not exist / be complete
        double center = Double.parseDouble(this.dataStore.get("center"));
        double range = Double.parseDouble(this.dataStore.get("range"));
        
        Description a1  = pathXmlHandler.getPath("a");
        Description a2  = pathXmlHandler.getPath("b");
        DistanceBoundCondition distance = 
                new DistanceBoundCondition("name", a1, a2, center, range);
        
        // add the condition to the parent description
        currentParent.addCondition(distance);
        
        // don't want to accumulate paths
        pathXmlHandler.clearPaths();
    }

}
