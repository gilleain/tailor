package tailor.engine.execute;

import java.util.List;

public interface Filter<T, S> {
    
    public List<T> filter(Iterable<S> iterable);

}
