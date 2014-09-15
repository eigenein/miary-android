package in.eigene.miary.core.backup;

import in.eigene.miary.core.*;

/**
 * Represents backup output format.
 */
public abstract class BackupOutput {

    public abstract void start();

    public abstract void write(final Note note);

    public abstract void finish();
}
