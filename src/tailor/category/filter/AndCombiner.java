package tailor.category.filter;

import java.util.List;
import java.util.stream.Collectors;

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
