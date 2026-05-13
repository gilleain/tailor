package tailor.api;

public interface Condition<T> {

	public boolean accept(T value);
}
