package aigen.description;

import java.util.HashMap;
import java.util.Map;

class PredefinedData {
	 public static final Map<String, double[][]> PREDEFINED_BOUNDS = new HashMap<>();
	 public static final Map<String, String[]> PREDEFINED_VALUES = new HashMap<>();
	 
	 static {
	     PREDEFINED_BOUNDS.put("AR", new double[][]{{-90, 30}, {0, 30}});
	     PREDEFINED_BOUNDS.put("AL", new double[][]{{90, 30}, {0, 30}});
	     PREDEFINED_BOUNDS.put("BR", new double[][]{{-90, 30}, {150, 30}});
	     
	     PREDEFINED_VALUES.put("RLnest", new String[]{"AR", "AL"});
	     PREDEFINED_VALUES.put("LRNest", new String[]{"AL", "AR"});
	     PREDEFINED_VALUES.put("RCatmat3", new String[]{"AR", "BR"});
	     PREDEFINED_VALUES.put("RCatmat4", new String[]{"AR", "AR", "BR"});
	 }
	}
