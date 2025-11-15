package tailor.category.aigen;

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
