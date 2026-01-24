package tailor.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import tailor.category.filter.Bound;
import tailor.category.filter.Filter;
import tailor.category.filter.PairFilter;

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
	private List<Row> members;	// TODO - remove memberIdList in favour of members
	private Map<PairIndex, PairFilter> regionMappings;
	
	/**
	 * @param name an arbitrary name for this category
	 */
	public Category(String name) {
		this.name = name;
		this.regionMappings = new HashMap<>();
		this.members = new ArrayList<>();
	}
	
	public Category(String name, Map<PairIndex, PairFilter> regionMappings) {
		this.name = name;
        this.regionMappings = regionMappings;
        this.members = new ArrayList<>();
	}
	
	public Category shallowCopy() {
		return new Category(this.name, this.regionMappings);
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
	
	public List<Row> getMembers() {
		return this.members;
	}
    
    public List<String> getMemberIds() {
		return getMembers().stream().map(Row::id).toList();
	}

	public String getName() {
        return this.name;
    }
    
//    // TODO - what is this method doing?
    public List<String> getBoundsStrings() {
        List<double[]> bounds = new ArrayList<>();
        int[] columnNumbers = getColumnIndexes();
        for (int i = 0; i < columnNumbers.length; i++) {
            bounds.add(new double[]{Double.MAX_VALUE, -Double.MAX_VALUE});
        }
        
        for (Row member : this.members) {
            List<Double> slice = multislice(member.values(), columnNumbers);
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
        // TODO
//        for (Filter filter : this.filters) {
//            if (filter instanceof Bound) {
//                bounds.add((Bound) filter);
//            }
//        }
        return bounds;
    }

    public int getNumberOfFilters() {
        return this.regionMappings.size();	// TODO - slightly confusing
    }

    public void addBound(Bound bound) {
//        this.filters.add(bound);
    	// TODO
    }

    public boolean accepts(double[] values) {
	    for (PairIndex pairIndex : this.regionMappings.keySet()) {
	    	PairFilter filter = this.regionMappings.get(pairIndex);
	    	double firstValue = values[pairIndex.firstColumn()];
	    	double secondValue = values[pairIndex.secondColumn()];
			if (filter.accept(firstValue, secondValue)) {
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
            String pdbid = member.id();
            int[] range1 = getStartEnd(member.startEnd());
            int s1 = range1[0];
            int e1 = range1[1];
            
            for (Row otherMember : other.members) {
                if (!pdbid.equals(otherMember.id())) continue;
                
                int[] range2 = getStartEnd(otherMember.startEnd());
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
		int[] columnNumbers = getColumnIndexes();
		
		List<Double> means = new ArrayList<>();
		for (int i = 0; i < columnNumbers.length; i++) {
			means.add(0.0);
		}

		for (Row member : this.members) {
			List<Double> slice = multislice(member.values(), columnNumbers);
			for (int i = 0; i < slice.size(); i++) {
				double value = slice.get(i);
				means.set(i, means.get(i) + value);
			}
		}

		return means.stream()
				.map(mean -> mean / getCount())
				.collect(Collectors.toList());
	}
	
	private int[] getColumnIndexes() {
		int numberOfIndexes = regionMappings.size() * 2;
		int[] columnNumbers = new int[numberOfIndexes];
		List<PairIndex> pairIndexes = regionMappings.keySet().stream().collect(Collectors.toList());
		
		for (int i = 0; i < numberOfIndexes; i += 2) {
			int pairIndexIndex = (i == 0)? 0 : i / 2;
			PairIndex pair = pairIndexes.get(pairIndexIndex);
			columnNumbers[i] = pair.firstColumn();
			columnNumbers[i + 1] = pair.secondColumn();
		}
		return columnNumbers;
	}


	public List<MeanStdDev> getMeanWithStdDevs() {
		int[] columnNumbers = getColumnIndexes();
		List<Double> stdDevs = new ArrayList<>();
		for (int i = 0; i < columnNumbers.length; i++) {
			stdDevs.add(0.0);
		}

		List<Double> means = getMeans();

		for (Row member : this.members) {
			List<Double> slice = multislice(member.values(), columnNumbers);
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

	public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (PairFilter filter : this.regionMappings.values()) {
            stringBuffer.append(filter).append(" ");
        }
		return this.name + " " + this.getCount() + " " + stringBuffer;
	}
	
	// Helper methods
    private static int[] getStartEnd(String rowData) {
        String[] parts = rowData.split(" ")[1].split("-");
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
    }
    
    private static List<Double> multislice(double[] alist, int[] indices) {
        List<Double> slice = new ArrayList<>();
        for (int index : indices) {
            slice.add(alist[index]);
        }
        return slice;
    }
	
}
