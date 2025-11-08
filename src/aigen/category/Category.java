package aigen.category;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Read in the output of a tailor search and count the numbers in predefined categories
 */
public class Category implements Iterable<String[]> {
    private String name;
    private List<Integer> columnNumbers;
    private List<Condition> conditions;
    private List<String[]> members;
    
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
    public Iterator<String[]> iterator() {
        return members.iterator();
    }
    
    public int getCount() {
        return members.size();
    }
    
    public List<String[]> filter(Filter filter) {
        List<String[]> filteredMembers = new ArrayList<>();
        for (String[] member : this) {
            if (filter.accepts(member)) {
                filteredMembers.add(member);
            }
        }
        return filteredMembers;
    }
    
    public boolean accepts(String[] bits) {
        for (int i = 0; i < columnNumbers.size(); i++) {
            int columnNumber = columnNumbers.get(i);
            String value = bits[columnNumber];
            Condition condition = conditions.get(i);
            if (!condition.satisfiedBy(value)) {
                return false;
            }
        }
        return true;
    }
    
    public void addMember(String[] bits) {
        this.members.add(bits);
    }
    
    public List<String> getBounds() {
        List<double[]> bounds = new ArrayList<>();
        for (int i = 0; i < columnNumbers.size(); i++) {
            bounds.add(new double[]{Double.MAX_VALUE, -Double.MAX_VALUE});
        }
        
        for (String[] member : this) {
            List<String> slice = multislice(member, columnNumbers);
            for (int i = 0; i < slice.size(); i++) {
                double value = Double.parseDouble(slice.get(i));
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
        
        for (String[] member : this) {
            List<String> slice = multislice(member, columnNumbers);
            for (int i = 0; i < slice.size(); i++) {
                double value = Double.parseDouble(slice.get(i));
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
        
        for (String[] member : this) {
            List<String> slice = multislice(member, columnNumbers);
            for (int i = 0; i < slice.size(); i++) {
                double value = Double.parseDouble(slice.get(i));
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
        
        for (String[] member : this) {
            String pdbid = member[0];
            int[] range1 = getStartEnd(member);
            int s1 = range1[0];
            int e1 = range1[1];
            
            for (String[] otherMember : other) {
                if (!pdbid.equals(otherMember[0])) continue;
                
                int[] range2 = getStartEnd(otherMember);
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
    private static int[] getStartEnd(String[] rowData) {
        String[] parts = rowData[1].split(" ")[1].split("-");
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
    }
    
    private static List<String> multislice(String[] alist, List<Integer> indices) {
        List<String> slice = new ArrayList<>();
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
 * Filter interface
 */
interface Filter {
    boolean accepts(String[] row);
}

/**
 * Condition interface for value checking
 */
interface Condition {
    boolean satisfiedBy(String value);
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
    public boolean accepts(String[] row) {
        for (Filter filter : filters) {
            if (!filter.accepts(row)) {
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
    public boolean accepts(String[] row) {
        return Double.parseDouble(row[columnIndex]) <= maxValue;
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
    public boolean accepts(String[] row) {
        return Double.parseDouble(row[columnIndex]) >= minValue;
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
    public boolean satisfiedBy(String value) {
        double val = Double.parseDouble(value);
        return min < val && val < max;
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
    public boolean satisfiedBy(String value) {
        double val = Double.parseDouble(value);
        return (-180 < val && val < lower) || (upper < val && val < 180);
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
    
    public void assign(String[] row) {
        for (Category category : categories) {
            if (category.accepts(row)) {
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
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            fromLines(lines);
        }
    }
    
    public void fromLines(List<String> lines) {
        // Skip header line
        for (int i = 1; i < lines.size(); i++) {
            String[] bits = lines.get(i).replace("\n", "").split("\t");
            assign(bits);
        }
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
