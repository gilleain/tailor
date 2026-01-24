package tailor.category.filter;


/**
 * @author maclean
 *
 */
public class PropertyFilter implements Filter {

    private String targetValue;
    
    public PropertyFilter(String targetValue) {
        this.targetValue = targetValue;
    }
    
    public boolean accept(double value) {
        // TODO : this is clearly nonsense!
        return String.valueOf(value).equals(this.targetValue); 
    }

}
