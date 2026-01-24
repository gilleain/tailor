package tailor.category;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import tailor.category.filter.Filter;
import tailor.category.filter.PairFilter;

/**
 * Set of categories for classification
 */
class CategorySet implements Iterable<Category> {
    private List<Category> categories;
    private List<Integer> columnNumbers;
    private Map<String, PairFilter> regions;
    private List<PairIndex> indexList;
    private Category unmatched;
    
    public CategorySet() {
        this(new ArrayList<>(), new HashMap<>());
    }
    
    public CategorySet(List<PairIndex> indexList, Map<String, PairFilter> regions) {
        this.categories = new ArrayList<>();
        this.indexList = indexList;
        this.regions = regions;
        this.unmatched = new Category("?");
    }
    
    public void clear() {
        for (Category category : categories) {
            category.clear();
        }
        unmatched.clear();
    }
    
    public int countInCategories(List<String> categoryNameList) {
        int count = 0;
        for (String categoryName : categoryNameList) {
            Category cat = getCategory(categoryName);
            if (cat != null) {
                count += cat.getCount();
            }
        }
        return count;
    }
    
    public Category getUnmatched() {
        return unmatched;
    }
    
    public Category getCategory(String categoryName) {
        for (Category category : this) {
            if (category.getName().equals(categoryName)) {
                return category;
            }
        }
        return null;
    }
    
    @Override
    public Iterator<Category> iterator() {
        return categories.iterator();
    }
    
    public void createCategoriesWithNames(List<String> names) {
        for (String name : names) {
            String[] regionKeys = name.split("");
            createCategoryFromRegions(regionKeys);
        }
    }
    
    public void createCategoryFromRegions(String... regionKeys) {
    	// check we have a mapping for each region key
    	assert regionKeys.length == indexList.size();	

    	Map<PairIndex, PairFilter> regionMappings = new HashMap<>();
    	
    	for (int index = 0; index < regionKeys.length; index++) {
    		PairFilter region = regions.get(regionKeys[index]);
    		PairIndex pairIndex = indexList.get(index);
    		regionMappings.put(pairIndex, region);
    	}
        String categoryName = String.join("", regionKeys);
        
        // Create categories from a name and a map of column indexes to regions
        categories.add(new Category(categoryName, regionMappings));
    }
    
    
    public void assign(Row row) {
        for (Category category : categories) {
            if (category.accepts(row.values())) {
                category.addMember(row);
                return;
            }
        }
        unmatched.addMember(row);
    }
    
    public int getTotal() {
        int total = 0;
        for (Category category : categories) {
            total += category.getCount();
        }
        total += unmatched.getCount();
        return total;
    }
    
    public List<OverlapCount> getOverlapCounts(CategorySet otherCategorySet) {
        List<OverlapCount> overlapCounts = new ArrayList<>();
        for (Category category : this) {
            for (Category other : otherCategorySet) {
                Map<Integer, Integer> offsets = category.countOverlap(other);
                overlapCounts.add(new OverlapCount(
                    category.getName(), category.getCount(),
                    other.getName(), other.getCount(),
                    offsets
                ));
            }
        }
        return overlapCounts;
    }
    
    public void printResults() {
        for (Category category : this) {
            if (category.getCount() > 1) {
                String meanStdDev = category.getMeanWithStdDevs().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(" "));
                System.out.printf("%s %4d %s%n", category.getName(), category.getCount(), meanStdDev);
            } else {
                System.out.printf("%s %4d%n", category.getName(), category.getCount());
            }
        }
        
        if (unmatched.getCount() > 0) {
        	String unmatchedBounds = String.join(" ", unmatched.getBoundsStrings()); 
            System.out.printf("%s %4d %s%n", unmatched.getName(), unmatched.getCount(), unmatchedBounds);
        } else {
            System.out.printf("%s %4d%n", unmatched.getName(), unmatched.getCount());
        }
    }
    
    public void fromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
            	if (index == 0) {
            		index++;
            		continue;
            	}
                fromLine(line);
            }
        }
    }
    
    public void fromLine(String line) {
        // Skip header line
    	String[] bits = line.replace("\n", "").split("\t");
    	double[] values = new double[bits.length - 2];
    	for (int i = 0; i < values.length; i++) {
    		values[i] = Double.valueOf(bits[i + 2]);
    	}
    	assign(new Row(bits[0], bits[1], values));
    }
    
    public CategorySet shallowCopy() {
        CategorySet copy = new CategorySet(this.indexList, this.regions);
        copy.categories = this.categories.stream()
            .map(Category::shallowCopy)
            .collect(Collectors.toList());
        return copy;
    }
    
    @Override
    public String toString() {
        return String.format("%d categories covering %d members", categories.size(), getTotal());
    }
}
