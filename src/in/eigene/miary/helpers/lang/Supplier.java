package in.eigene.miary.helpers.lang;

/**
 * Represents a supplier of results.
 */
public interface Supplier<TValue> {
    TValue get();
}
