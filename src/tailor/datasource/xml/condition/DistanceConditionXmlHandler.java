package tailor.datasource.xml.condition;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import tailor.datasource.xml.PathXmlHandler;
import tailor.description.ChainDescription;
import tailor.description.DescriptionPath;
import tailor.description.atom.AtomDistanceRangeDescription;

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
    public void complete(ChainDescription currentParent, PathXmlHandler pathXmlHandler) {
        // TODO : these data items might not exist / be complete
        double center = Double.parseDouble(this.dataStore.get("center"));
        double range = Double.parseDouble(this.dataStore.get("range"));
        
        DescriptionPath a1  = pathXmlHandler.getPath("a");
        DescriptionPath a2  = pathXmlHandler.getPath("b");
        AtomDistanceRangeDescription distance = 
                new AtomDistanceRangeDescription("name", center - range, center + range, a1, a2);
        
        // add the condition to the parent description
        currentParent.addAtomListDescriptions(distance);
        
        // don't want to accumulate paths
        pathXmlHandler.clearPaths();
    }

}
