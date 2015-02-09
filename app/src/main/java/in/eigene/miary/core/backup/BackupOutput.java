package in.eigene.miary.core.backup;

import in.eigene.miary.core.classes.*;
import in.eigene.miary.exceptions.*;

import java.io.*;

/**
 * Represents backup output format.
 */
public abstract class BackupOutput {

    protected final OutputStreamWriter streamWriter;

    private final String name;

    public abstract String getMimeType();

    public abstract void start(final int count) throws IOException;

    public abstract void write(final Note note) throws IOException;

    public abstract void finish() throws IOException;

    protected BackupOutput(final String name, final OutputStream outputStream) {
        this.name = name;

        OutputStreamWriter streamWriter;
        try {
            streamWriter = new OutputStreamWriter(outputStream, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            InternalRuntimeException.throwForException("Unsupported encoding.", e);
            streamWriter = null;
        }
        this.streamWriter = streamWriter;
    }

    public String getName() {
        return name;
    }

    public static abstract class Factory {

        public abstract BackupOutput createOutput(final Storage storage) throws IOException;
    }
}
