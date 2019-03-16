package tailor.condition.alt;

public interface UniformCondition<T> {
    
    @SuppressWarnings("unchecked")
    boolean allows(T... entities);

}
