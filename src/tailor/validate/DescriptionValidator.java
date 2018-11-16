package tailor.validate;

import java.util.ArrayList;
import java.util.List;

import tailor.description.Description;

/**
 * Use this to check that a Description is valid; which specifically means that
 * it passes all the tests set by its component Validator instances.
 *   
 * @author maclean
 *
 */
public class DescriptionValidator {
    
    /**
     * The classes that do the work of description validation.
     */
    private List<Validator> validators;
    
    /**
     * Make a validator with the standard set of checks.
     */
    public DescriptionValidator() {
        validators = new ArrayList<>();
        validators.add(new MeasuresAtTopLevel());
    }
    
    /**
     * Check a Description against all the Validator instances.
     * 
     * @param description the description to check
     * @return true if all the validators allow the description
     */
    public boolean isValid(Description description) {
        for (Validator validator : validators) {
            if (validator.isValid(description)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

}
