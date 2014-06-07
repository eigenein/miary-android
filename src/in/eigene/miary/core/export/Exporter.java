package in.eigene.miary.core.export;

import android.content.*;
import android.os.*;

import java.io.*;
import java.text.*;
import java.util.*;

public class Exporter {

    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public static void start(final Context context) {
        // Make file path.
        final String date = DATE_FORMAT.format(new Date());
        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        final File archive = new File(path, String.format("Miary Export %s.zip", date));
        // Start export task.
        new ExportAsyncTask(context, archive).execute();
    }
}
