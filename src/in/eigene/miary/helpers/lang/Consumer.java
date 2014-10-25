package in.eigene.miary.helpers.lang;

/**
 * Represents an operation that accepts a single input argument and returns no result.
 */
public interface Consumer<TValue> {
    void accept(final TValue value);
}
