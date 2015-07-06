package in.eigene.miary.backup.storages;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import in.eigene.miary.backup.BackupOutput;
import in.eigene.miary.backup.Storage;

/**
 * Fake Parse Local Datastore storage.
 */
public class LocalDatastoreStorage extends Storage {

    @Override
    public boolean checkReady() {
        return true;
    }

    @Override
    public String getOutputName(final String suffix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream getOutputStream(final String name) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void finish(final Context context, final boolean uiThread, final BackupOutput output) {
        throw new UnsupportedOperationException();
    }

    public class Input extends Storage.Input {

        @Override
        public InputStream getInputStream() throws IOException {
            return new FakeInputStream();
        }

        /**
         * We can't return null thus we'll return this fake stream.
         */
        private class FakeInputStream extends InputStream {

            @Override
            public int read() throws IOException {
                throw new UnsupportedOperationException();
            }
        }
    }
}
