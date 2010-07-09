package tailor.validate;

import tailor.description.Description;

/**
 * A validator does a single check on a Description via its 
 * isValid(Description) method. The DescriptionValidator is composed of a 
 * set of these Validators.
 * 
 * @author maclean
 *
 */
public interface Validator {

    /**
     * Check the description to see if it is valid.
     * 
     * @param description the description to check
     * @return true if valid
     */
    public boolean isValid(Description description);  
}
