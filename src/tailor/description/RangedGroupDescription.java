package tailor.description;

public class RangedGroupDescription extends GroupDescription {

    private int min;
    
    private int max;
    
    public void setMin(int min) {
        this.min = min;
    }
    
    public void setMax(int max) {
        this.max = max;
    }

    public int range() {
        return 0;
    }
}
