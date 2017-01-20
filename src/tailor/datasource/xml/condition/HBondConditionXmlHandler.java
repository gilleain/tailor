package tailor.datasource.xml.condition;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import tailor.condition.HBondCondition;
import tailor.datasource.xml.PathXmlHandler;
import tailor.description.Description;

public class HBondConditionXmlHandler implements ConditionXmlHandler {
    
    private Map<String, String> dataStore;
    
    public HBondConditionXmlHandler() {
        this.dataStore = new HashMap<String, String>();
    }

    @Override
    public void create(Attributes attrs) {
        this.dataStore.put("haMax", attrs.getValue("haMax"));
        this.dataStore.put("dhaMin", attrs.getValue("dhaMin"));
        this.dataStore.put("haaMin", attrs.getValue("haaMin"));
        if (attrs.getIndex("isNegated") != -1) {
            this.dataStore.put("isNegated", attrs.getValue("isNegated"));
        }
    }

    @Override
    public void complete(Description currentParent, PathXmlHandler pathXmlHandler) {
        // TODO : these data items might not exist / be complete
        double haMax = Double.parseDouble(this.dataStore.get("haMax"));
        double dhaMin = Double.parseDouble(this.dataStore.get("dhaMin"));
        double haaMin = Double.parseDouble(this.dataStore.get("haaMin"));
        
        Description d  = pathXmlHandler.getPath("donor");
        Description h  = pathXmlHandler.getPath("hydrogen");
        Description a  = pathXmlHandler.getPath("acceptor");
        Description aa = pathXmlHandler.getPath("attached");
        HBondCondition hbond = new HBondCondition(d, h, a, aa, haMax, dhaMin, haaMin);
        
        if (this.dataStore.containsKey("isNegated")) {
            boolean isNegated = Boolean.parseBoolean(this.dataStore.get("isNegated"));
            System.err.println("setting negated to " + isNegated);
            hbond.setNegated(isNegated);
        }
        
        // add the condition to the parent description
        currentParent.addCondition(hbond);
        
        // don't want to accumulate paths
        pathXmlHandler.clearPaths();
    }

}
