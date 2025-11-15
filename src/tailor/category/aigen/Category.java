package tailor.category.aigen;

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

/**
 * Read in the output of a tailor search and count the numbers in predefined categories
 */

class Row {	// TODO - could be record
	public double[] values;
	public String id; // TODO
	public String startEnd;	// TODO
	public Row(String id, String startEnd, double[] values) {
		this.id = id;
		this.startEnd = startEnd;
		this.values = values;
	}
}

public class Category implements Iterable<Row> {
	
    private String name;
    private List<Integer> columnNumbers;
    private List<Condition> conditions;
    private List<Row> members;
    
    public Category(String name) {
        this(name, new ArrayList<>(), new ArrayList<>());
    }
    
    public Category(String name, List<Integer> columnNumbers, List<Condition> conditions) {
        this.name = name;
        this.columnNumbers = columnNumbers;
        this.conditions = conditions;
        this.members = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }
    
    public void clear() {
        this.members.clear();
    }
    
    @Override
    public Iterator<Row> iterator() {
        return members.iterator();
    }
    
    public int getCount() {
        return members.size();
    }
    
    public List<double[]> filter(Filter filter) {
        List<double[]> filteredMembers = new ArrayList<>();
        for (Row member : this) {
            if (filter.accept(member.values)) {
                filteredMembers.add(member.values);
            }
        }
        return filteredMembers;
    }
    
    public boolean accepts(double[] bits) {
        for (int i = 0; i < columnNumbers.size(); i++) {
            int columnNumber = columnNumbers.get(i);
            double value = bits[columnNumber];
            Condition condition = conditions.get(i);
            if (!condition.satisfiedBy(value)) {
                return false;
            }
        }
        return true;
    }
    
    public void addMember(String id, String startEnd, double[] bits) {
        this.members.add(new Row(id, startEnd, bits));
    }
    
    
    public void addMember(Row row) {
        this.members.add(row);
    }
    
    public List<String> getBounds() {
        List<double[]> bounds = new ArrayList<>();
        for (int i = 0; i < columnNumbers.size(); i++) {
            bounds.add(new double[]{Double.MAX_VALUE, -Double.MAX_VALUE});
        }
        
        for (Row member : this) {
            List<Double> slice = multislice(member.values, columnNumbers);
            for (int i = 0; i < slice.size(); i++) {
                double value = slice.get(i);
                double[] bound = bounds.get(i);
                if (value < bound[0]) {
                    bound[0] = value;
                }
                if (value > bound[1]) {
                    bound[1] = value;
                }
            }
        }
        
        return bounds.stream()
            .map(b -> String.format("%4.0f to %4.0f", b[0], b[1]))
            .collect(Collectors.toList());
    }
    
    public List<Double> getMeans() {
        List<Double> means = new ArrayList<>();
        for (int i = 0; i < conditions.size(); i++) {
            means.add(0.0);
        }
        
        for (Row member : this) {
            List<Double> slice = multislice(member.values, columnNumbers);
            for (int i = 0; i < slice.size(); i++) {
                double value = slice.get(i);
                means.set(i, means.get(i) + value);
            }
        }
        
        return means.stream()
            .map(mean -> mean / getCount())
            .collect(Collectors.toList());
    }
    
    public List<MeanStdDev> getMeanWithStdDevs() {
        List<Double> stdDevs = new ArrayList<>();
        for (int i = 0; i < conditions.size(); i++) {
            stdDevs.add(0.0);
        }
        
        List<Double> means = getMeans();
        
        for (Row member : this) {
            List<Double> slice = multislice(member.values, columnNumbers);
            for (int i = 0; i < slice.size(); i++) {
                double value = slice.get(i);
                double x = value - means.get(i);
                stdDevs.set(i, stdDevs.get(i) + x * x);
            }
        }
        
        List<MeanStdDev> result = new ArrayList<>();
        for (int i = 0; i < stdDevs.size(); i++) {
            double stdDev = Math.sqrt(stdDevs.get(i) / (getCount() - 1));
            result.add(new MeanStdDev(means.get(i), stdDev));
        }
        
        return result;
    }
    
    public Map<Integer, Integer> countOverlap(Category other) {
        Map<Integer, Integer> offsets = new HashMap<>();
        
        for (Row member : this) {
            String pdbid = member.id;
            int[] range1 = getStartEnd(member.startEnd);
            int s1 = range1[0];
            int e1 = range1[1];
            
            for (Row otherMember : other) {
                if (!pdbid.equals(otherMember.id)) continue;
                
                int[] range2 = getStartEnd(otherMember.startEnd);
                int s2 = range2[0];
                int e2 = range2[1];
                
                if ((s2 < s1 && s1 < e2) || (s1 < s2 && s2 < e1) || 
                    (s2 < e1 && e1 < e2) || (s1 < e2 && e2 < e1)) {
                    int offset = s2 - s1;
                    offsets.put(offset, offsets.getOrDefault(offset, 0) + 1);
                }
            }
        }
        
        return offsets;
    }
    
    public Category shallowCopy() {
        return new Category(this.name, this.columnNumbers, this.conditions);
    }
    
    @Override
    public String toString() {
        return String.format("%s %s %s", name, columnNumbers, conditions);
    }
    
    // Helper methods
    private static int[] getStartEnd(String rowData) {
        String[] parts = rowData.split(" ")[1].split("-");
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
    }
    
    private static List<Double> multislice(double[] alist, List<Integer> indices) {
        List<Double> slice = new ArrayList<>();
        for (int index : indices) {
            slice.add(alist[index]);
        }
        return slice;
    }
}

/**
 * Helper class for mean and standard deviation
 */
class MeanStdDev {
    public double mean;
    public double stdDev;
    
    public MeanStdDev(double mean, double stdDev) {
        this.mean = mean;
        this.stdDev = stdDev;
    }
    
    @Override
    public String toString() {
        return String.format("%4.0f +/- %4.0f", mean, stdDev);
    }
}


/**
 * Condition interface for value checking
 */
interface Condition {
    boolean satisfiedBy(double value);
}

/**
 * Combines multiple filters with AND logic
 */
class AndCombiner implements Filter {
    private List<Filter> filters;
    
    public AndCombiner(List<Filter> filters) {
        this.filters = filters;
    }
    
    @Override
    public boolean accept(double[] row) {
        for (Filter filter : filters) {
            if (!filter.accept(row)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "AND [" + filters.stream()
            .map(Object::toString)
            .collect(Collectors.joining(", ")) + "]";
    }
}

/**
 * Upper bound filter
 */
class UpperBound implements Filter {
    private double maxValue;
    private int columnIndex;
    
    public UpperBound(double maxValue, int columnIndex) {
        this.maxValue = maxValue;
        this.columnIndex = columnIndex;
    }
    
    @Override
    public boolean accept(double[] row) {
        return row[columnIndex] <= maxValue;
    }
    
    @Override
    public String toString() {
        return "upper bound " + maxValue;
    }
}

/**
 * Lower bound filter
 */
class LowerBound implements Filter {
    private double minValue;
    private int columnIndex;
    
    public LowerBound(double minValue, int columnIndex) {
        this.minValue = minValue;
        this.columnIndex = columnIndex;
    }
    
    @Override
    public boolean accept(double[] row) {
        return row[columnIndex] >= minValue;
    }
    
    @Override
    public String toString() {
        return "lower bound " + minValue;
    }
}

/**
 * Range condition
 */
class Range implements Condition {
    private double min;
    private double max;
    
    public Range(double min, double max) {
        this.min = min;
        this.max = max;
    }
    
    @Override
    public boolean satisfiedBy(double value) {
        return min < value && value < max;
    }
    
    @Override
    public String toString() {
        return String.format("range (%s - %s)", min, max);
    }
}

/**
 * Split range condition (wraps around at boundaries)
 */
class SplitRange implements Condition {
    private double lower;
    private double upper;
    
    public SplitRange(double lower, double upper) {
        this.lower = lower;
        this.upper = upper;
    }
    
    @Override
    public boolean satisfiedBy(double value) {
        return (-180 < value && value < lower) || (upper < value && value < 180);
    }
    
    @Override
    public String toString() {
        return String.format("splitrange (%s - %s)", lower, upper);
    }
}

/**
 * Set of categories for classification
 */
class CategorySet implements Iterable<Category> {
    private List<Category> categories;
    private List<Integer> columnNumbers;
    private Map<String, List<Condition[]>> regions;
    private Category unmatched;
    
    public CategorySet() {
        this(new ArrayList<>(), new HashMap<>());
    }
    
    public CategorySet(List<Integer> columnNumbers, Map<String, List<Condition[]>> regions) {
        this.categories = new ArrayList<>();
        this.columnNumbers = columnNumbers;
        this.regions = regions;
        this.unmatched = new Category("?", columnNumbers, new ArrayList<>());
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
        List<Condition[]> conditionPairs = new ArrayList<>();
        for (String key : regionKeys) {
            conditionPairs.add(regions.get(key).get(0));
        }
        String categoryName = String.join("", regionKeys);
        createCategory(categoryName, conditionPairs);
    }
    
    /**
     * Create categories from a name and a list of pairs of conditions
     */
    public void createCategory(String categoryName, List<Condition[]> conditionPairs) {
        List<Condition> conditions = new ArrayList<>();
        for (Condition[] pair : conditionPairs) {
            conditions.add(pair[0]);
            conditions.add(pair[1]);
        }
        categories.add(new Category(categoryName, columnNumbers, conditions));
    }
    
    public void assign(Row row) {
        for (Category category : categories) {
            if (category.accepts(row.values)) {
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
            System.out.printf("%s %4d %s%n", unmatched.getName(), unmatched.getCount(), 
                String.join(" ", unmatched.getBounds()));
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
        CategorySet copy = new CategorySet(this.columnNumbers, this.regions);
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

/**
 * Helper class for overlap count data
 */
class OverlapCount {
    public String categoryName;
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
