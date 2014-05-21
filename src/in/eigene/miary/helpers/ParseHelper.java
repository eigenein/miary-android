package in.eigene.miary.helpers;

import android.content.*;
import com.parse.*;
import in.eigene.miary.core.*;

public class ParseHelper {

    private static final String APPLICATION_ID = "jpnD20rkM3xxna9OhRtun2IbzE7QjPEULtEmIRKC";
    private static final String CLIENT_KEY = "ChviiekJmgXCOcQuuzNnifiIHjQ3vHa2GqYW4yCC";

    public static void initialize(final Context context) {
        Parse.enableLocalDatastore(context);
        ParseObject.registerSubclass(Note.class);
        Parse.initialize(context, APPLICATION_ID, CLIENT_KEY);
    }
}
