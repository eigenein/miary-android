package in.eigene.miary.backup;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import in.eigene.miary.persistence.Note;
import in.eigene.miary.exceptions.InternalRuntimeException;

public abstract class RestoreInput {

    protected final InputStreamReader streamReader;

    protected RestoreInput(final InputStream inputStream) {
        InputStreamReader streamReader;
        try {
            streamReader = new InputStreamReader(inputStream, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            InternalRuntimeException.throwForException("Unsupported encoding.", e);
            streamReader = null;
        }
        this.streamReader = streamReader;
    }

    public abstract int start() throws IOException;

    public abstract Note read() throws IOException;

    public abstract void finish() throws IOException;

    public static abstract class Factory {

        public abstract RestoreInput createInput(final InputStream inputStream);
    }
}
