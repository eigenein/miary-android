package in.eigene.miary.helpers.lang;

/**
 * Represents a function that accepts one argument and produces a result.
 */
public interface Function<TValue, TResult> {
    /**
     * Applies this function to the given argument.
     */
    public TResult apply(TValue value);
}
