package tailor.datasource.xml.condition;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import tailor.datasource.xml.PathXmlHandler;
import tailor.description.ChainDescription;
import tailor.description.DescriptionPath;
import tailor.description.atom.AtomTorsionRangeDescription;

public class TorsionConditionXmlHandler implements ConditionXmlHandler {
    
    private Map<String, String> dataStore;
    
    public TorsionConditionXmlHandler() {
        this.dataStore = new HashMap<>();
    }

    @Override
    public void create(Attributes attrs) {
        this.dataStore.put("name", attrs.getValue("name"));
        this.dataStore.put("midPoint", attrs.getValue("midPoint"));
        this.dataStore.put("range", attrs.getValue("range"));
    }

    @Override
    public void complete(ChainDescription parent, PathXmlHandler pathXmlHandler) {
        String name = this.dataStore.get("name");
        double midPoint = Double.parseDouble(this.dataStore.get("midPoint"));
        double range = Double.parseDouble(this.dataStore.get("range"));
        
        DescriptionPath pathA  = pathXmlHandler.getPath("a");
        DescriptionPath pathB  = pathXmlHandler.getPath("b");
        DescriptionPath pathC  = pathXmlHandler.getPath("c");
        DescriptionPath pathD = pathXmlHandler.getPath("d");
        
        // TODO - is it range or half range?
        double minValue = midPoint - range;
        double maxValue = midPoint + range;
        
        AtomTorsionRangeDescription torsion = 
                new AtomTorsionRangeDescription(name, minValue, maxValue, pathA, pathB, pathC, pathD);
        
        // add the condition to the parent description
        parent.addAtomListDescriptions(torsion);
        
        // don't want to accumulate paths
        pathXmlHandler.clearPaths();
    }

}
