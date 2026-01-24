package tailor.category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import tailor.category.filter.PairFilter;
import tailor.category.filter.Range;
import tailor.category.filter.SquareRegion;


public class TestCategorySet {

	Map<String, PairFilter> regions = new HashMap<>() {{
            put("R", region(-180,   0, -180, 180));
            put("L", region(   0, 180, -180, 180));
            put("A", region(-150, -30,  -60,  50));
            put("a", region(  30, 150,  -30,  90));
            put("B", region(-180,  30,   90, 180));
     }};
     
     private static PairFilter region(double minX, double maxX, double minY, double maxY) {
    	 return new SquareRegion(new Range(minX, maxX), new Range(minY, maxY));
     }
     
     private CategorySet makeSet() {
    	 List<PairIndex> pairIndexes = new ArrayList<>();
    	 pairIndexes.add(new PairIndex(0, 1));
    	 pairIndexes.add(new PairIndex(2, 3));
    	 pairIndexes.add(new PairIndex(4, 5));
    	 
    	 CategorySet categorySet = new CategorySet(pairIndexes, regions);
    	 categorySet.createCategoryFromRegions("A", "A", "A");
    	 categorySet.createCategoryFromRegions("A", "A", "B");
    	 categorySet.createCategoryFromRegions("A", "B", "B");
    	 categorySet.createCategoryFromRegions("A", "B", "A");
    	 categorySet.createCategoryFromRegions("R", "R", "R");

    	 categorySet.createCategoryFromRegions("R", "B", "a");
    	 categorySet.createCategoryFromRegions("R", "B", "L");

    	 categorySet.createCategoryFromRegions("R", "A", "a");
    	 categorySet.createCategoryFromRegions("R", "A", "L");

    	 categorySet.createCategoryFromRegions("R", "R", "L");

    	 categorySet.createCategoryFromRegions("R", "L", "A");
    	 categorySet.createCategoryFromRegions("R", "a", "B");
    	 categorySet.createCategoryFromRegions("R", "L", "B");
    	 categorySet.createCategoryFromRegions("R", "L", "R");

    	 categorySet.createCategoryFromRegions("R", "a", "a");
    	 categorySet.createCategoryFromRegions("R", "L", "L");

    	 categorySet.createCategoryFromRegions("L", "R", "L");
    	 categorySet.createCategoryFromRegions("L", "L", "R");
    	 categorySet.createCategoryFromRegions("L", "R", "R");
    	 categorySet.createCategoryFromRegions("L", "L", "L");
    	 
    	 return categorySet;
     }
     
     @Test
     public void testBasic() throws IOException {
    	 String file = "results/alpha.txt";
    	 
    	 CategorySet categorySet = makeSet();
    	 categorySet.fromFile(file);

    	 categorySet.printResults();
     }

     @Test
     public void testGetOverlapCounts() throws IOException {
    	 String file1 = "";	// TODO
    	 String file2 = ""; // TODO

    	 CategorySet categorySet = makeSet();

    	 categorySet.fromFile(file1);

    	 CategorySet categorySetCopy = categorySet.shallowCopy();
    	 categorySetCopy.fromFile(file2);

    	 List<OverlapCount> overlapCounts = categorySet.getOverlapCounts(categorySetCopy);
    	 for (OverlapCount overlapCount : overlapCounts) {

    		 int count = 0;
    		 for (int offset : overlapCount.offsets.keySet()) {
    			 count += overlapCount.offsets.get(offset);
    		 }
    	 }
     }
}
