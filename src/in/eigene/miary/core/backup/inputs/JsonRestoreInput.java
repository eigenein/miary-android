package in.eigene.miary.core.backup.inputs;

import android.util.*;
import in.eigene.miary.core.backup.*;

import java.io.*;

public class JsonRestoreInput extends RestoreInput {

    final JsonReader reader;

    public JsonRestoreInput(final InputStream inputStream) {
        super(inputStream);
        this.reader = new JsonReader(streamReader);
    }

    public static class Factory extends RestoreInput.Factory {

        @Override
        public RestoreInput createInput(final InputStream inputStream) {
            return new JsonRestoreInput(inputStream);
        }
    }
}
