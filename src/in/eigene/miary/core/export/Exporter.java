package in.eigene.miary.core.export;

import in.eigene.miary.core.*;
import in.eigene.miary.exceptions.*;

import java.io.*;
import java.nio.charset.*;

public abstract class Exporter {

    private static final Charset CHARSET = Charset.forName("utf-8");

    public abstract int getWriterTitle();

    public abstract String getExtension();

    public abstract void putNote(final OutputStream stream, final Note note);

    protected static void print(final OutputStream stream, final String string) {
        final String line = String.format("%s%n", string);
        final byte[] buffer = line.getBytes(CHARSET);
        try {
            stream.write(buffer);
        } catch (final IOException e) {
            InternalRuntimeException.throwForException("Could not write a string.", e);
        }
    }
}
