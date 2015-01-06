package in.eigene.miary.core.backup;

import in.eigene.miary.core.classes.*;
import in.eigene.miary.exceptions.*;

import java.io.*;

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

    public abstract LocalNote read() throws IOException;

    public abstract void finish() throws IOException;

    public static abstract class Factory {

        public abstract RestoreInput createInput(final InputStream inputStream);
    }
}
