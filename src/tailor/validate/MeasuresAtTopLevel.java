package tailor.validate;

import tailor.description.Description;

/**
 * Checks that only the top-level description object has measures.
 * 
 * @author maclean
 *
 */
public class MeasuresAtTopLevel implements Validator {

    @Override
    public boolean isValid(Description description) {
        for (Description subdescription : description.getSubDescriptions()) {
            if (subDescriptionHasNoMeasures(subdescription)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }
    
    public boolean subDescriptionHasNoMeasures(Description description) {
        if (description.getMeasures().size() == 0) {
            for (Description subDescription : description.getSubDescriptions()) {
                if (subDescriptionHasNoMeasures(subDescription)) {
                    continue;
                } else {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

}
