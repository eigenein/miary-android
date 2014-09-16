package in.eigene.miary.core.backup;

import in.eigene.miary.core.*;

import java.io.*;

/**
 * Represents backup output format.
 */
public abstract class BackupOutput {

    public abstract void start() throws IOException;

    public abstract void write(final Note note) throws IOException;

    public abstract void finish() throws IOException;
}
