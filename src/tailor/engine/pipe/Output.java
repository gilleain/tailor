package tailor.engine.pipe;

public interface Output<T> {
    
    void accept(T t);

}
