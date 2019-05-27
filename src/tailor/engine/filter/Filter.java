package tailor.engine.filter;

import java.util.List;

public interface Filter<T, S> {
    
    public List<T> filter(Iterable<S> iterable);

}
