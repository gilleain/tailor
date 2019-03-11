package tailor.condition.alt;

public interface PropertyCondition<T> {

    boolean allows(T entity);
}
