package tailor.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import tailor.category.filter.Bound;
import tailor.category.filter.Filter;

/**
 * A Category is like a motif Description, except that it is less precise. It is
 * intended to be used as a filter for the measurement lists from motif searches.
 * A filter is some bound or constraint on one or more columns of the result table.
 * 
 * @author maclean
 *
 */
public class Category {

	private String name;
	private List<Filter> filters;
	private List<String> memberIdList;
	private List<Row> members;	// TODO - remove memberIdList in favour of members
	private List<Integer> columnNumbers;	// TODO - why do we need this?
	
	/**
	 * @param name an arbitrary name for this category
	 */
	public Category(String name) {
		this.name = name;
		this.filters = new ArrayList<>();
		this.memberIdList = new ArrayList<>();
	}
	
	public Category(String name, List<Integer> columnNumbers, List<Filter> conditions) {
		this.name = name;
        this.columnNumbers = columnNumbers;
        this.filters = conditions;
        this.members = new ArrayList<>();
	}
	
	public Category shallowCopy() {
		return new Category(this.name, this.columnNumbers, this.filters);
	}
	
    public int getCount() {
        return members.size();
    }
	
    public void clear() {
        this.members.clear();
    }

	public void addMember(Row row) {
        this.members.add(row);
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<Filter> getFilters() {
        return this.filters;
    }
    
    // TODO - what is this method doing?
    public List<String> getBoundsStrings() {
        List<double[]> bounds = new ArrayList<>();
        for (int i = 0; i < columnNumbers.size(); i++) {
            bounds.add(new double[]{Double.MAX_VALUE, -Double.MAX_VALUE});
        }
        
        for (Row member : this.members) {
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
    
    public List<Bound> getBounds() {
        List<Bound> bounds = new ArrayList<>();
        for (Filter filter : this.filters) {
            if (filter instanceof Bound) {
                bounds.add((Bound) filter);
            }
        }
        return bounds;
    }
    
    public Iterator<Filter> iterator() {
        return this.filters.iterator();
    }
    
    public Filter getFilter(int i) {
        return this.filters.get(i);
    }
    
    public int getNumberOfFilters() {
        return this.filters.size();
    }
	
	public void addFilter(Filter filter) {
		this.filters.add(filter);
	}
    
    public void addBound(Bound bound) {
        this.filters.add(bound);
    }
	
	public boolean accepts(double[] values) {
	    for (Filter filter : this.filters) {
			if (filter.accept(values)) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}
	
	public Map<Integer, Integer> countOverlap(Category other) {
        Map<Integer, Integer> offsets = new HashMap<>();
        
        for (Row member : this.members) {
            String pdbid = member.id;
            int[] range1 = getStartEnd(member.startEnd);
            int s1 = range1[0];
            int e1 = range1[1];
            
            for (Row otherMember : other.members) {
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

	public List<Double> getMeans() {
		List<Double> means = new ArrayList<>();
		for (int i = 0; i < filters.size(); i++) {
			means.add(0.0);
		}

		for (Row member : this.members) {
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
		for (int i = 0; i < filters.size(); i++) {
			stdDevs.add(0.0);
		}

		List<Double> means = getMeans();

		for (Row member : this.members) {
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


	public void addId(String id) {
		this.memberIdList.add(id);
	}
    
    public List<String> getMemberIds() {
        return this.memberIdList;
    }
    
    public int getNumberOfMembers() {
        return this.memberIdList.size();
    }
	
	public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (Filter filter : this.filters) {
            stringBuffer.append(filter).append(" ");
        }
		return this.name + " " + this.getNumberOfMembers() + " " + stringBuffer;
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
