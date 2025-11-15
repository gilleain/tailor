package tailor.category.filter;


/**
 * @author maclean
 *
 */
public class PropertyFilter implements Filter {

    private int columnIndex;
    private String targetValue;
    
    public PropertyFilter(int columnIndex, String targetValue) {
        this.columnIndex = columnIndex;
        this.targetValue = targetValue;
    }
    
    public boolean accept(double[] values) {
        // TODO : this is clearly nonsense!
        return String.valueOf(values[this.columnIndex]).equals(this.targetValue); 
    }

}
