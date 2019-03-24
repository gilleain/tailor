package tailor.condition.alt;

public interface UniformCondition<T> {
    
    /**
     * Check this condition applies to some entities.
     * 
     * @param entities
     * @return
     */
    @SuppressWarnings("unchecked")
    boolean allows(T... entities);
    
    /**
     * @return the number of entities this applies to
     */
    int arity();

}
