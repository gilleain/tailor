package tailor.category;

import java.util.Map;

/**
 * Helper class for overlap count data
 */
class OverlapCount {
    public String categoryName; // TODO - make these private and/or make this a record
    public int categoryCount;
    public String otherName;
    public int otherCount;
    public Map<Integer, Integer> offsets;
    
    public OverlapCount(String categoryName, int categoryCount, 
                       String otherName, int otherCount, 
                       Map<Integer, Integer> offsets) {
        this.categoryName = categoryName;
        this.categoryCount = categoryCount;
        this.otherName = otherName;
        this.otherCount = otherCount;
        this.offsets = offsets;
    }
}