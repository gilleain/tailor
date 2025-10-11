package aigen;

import java.util.HashMap;
import java.util.Map;

//Helper class for selection construction
class SelectionHelper {
public static Description constructSelection(String chainID, String chainType, 
                                            Integer residuePosition, String atomName) {
   if (chainID != null || chainType != null) {
       Map<String, Object> props = new HashMap<>();
       props.put("chainType", chainType);
       ChainDescription selection = new ChainDescription(props);
       
       Map<String, Object> residueProps = new HashMap<>();
       residueProps.put("position", residuePosition);
       selection.addResidue(new ResidueDescription(residueProps));
       
       // TODO - fix this compile error
//       selection.children.get(0).addAtom(atomName);
       
       return selection;
   } else {
       Map<String, Object> props = new HashMap<>();
       props.put("position", residuePosition);
       ResidueDescription selection = new ResidueDescription(props);
       selection.addAtom(atomName);
       return selection;
   }
}
}
