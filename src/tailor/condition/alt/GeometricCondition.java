package tailor.condition.alt;

public interface GeometricCondition<T> {

    boolean allows(T entity);
    
}
