package in.eigene.miary.core.backup.outputs;

import in.eigene.miary.core.backup.*;
import in.eigene.miary.exceptions.*;

import java.io.*;

public abstract class TextBackupOutput extends BackupOutput {

    protected final OutputStreamWriter streamWriter;

    private final String name;

    protected TextBackupOutput(final String name, final OutputStream outputStream) {
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

    @Override
    public String getName() {
        return name;
    }
}
