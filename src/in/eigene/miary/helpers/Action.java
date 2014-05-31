package in.eigene.miary.helpers;

public interface Action<TValue> {
    void done(final TValue value);
}
