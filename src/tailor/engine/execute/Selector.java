package tailor.engine.execute;

public interface Selector<T, S> {
    
    @SuppressWarnings("unchecked")
    T get(S... entities);

}
