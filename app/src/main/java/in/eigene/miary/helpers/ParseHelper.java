package in.eigene.miary.helpers;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class ParseHelper {

    private static final String APPLICATION_ID = "jpnD20rkM3xxna9OhRtun2IbzE7QjPEULtEmIRKC";
    private static final String CLIENT_KEY = "ChviiekJmgXCOcQuuzNnifiIHjQ3vHa2GqYW4yCC";

    public static void initialize(final Context context) {
        Parse.enableLocalDatastore(context);
        Parse.initialize(context, APPLICATION_ID, CLIENT_KEY);
    }

    /**
     * Links the installation to the current user.
     */
    public static void linkInstallation() {
        final ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", ParseUser.getCurrentUser());
        installation.saveInBackground();
    }
}
