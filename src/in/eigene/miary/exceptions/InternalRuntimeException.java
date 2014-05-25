package in.eigene.miary.exceptions;

public class InternalRuntimeException extends RuntimeException {

    /**
     * Throws exception if cause is not null.
     */
    public static void throwForException(final String message, final Throwable cause) {
        if (cause != null) {
            throw new InternalRuntimeException(message, cause);
        }
    }

    private InternalRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
