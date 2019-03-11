package tailor.condition.alt;

public interface Condition<T> {
    
    @SuppressWarnings("unchecked")
    boolean allows(T... entities);

}
