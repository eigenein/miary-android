package in.eigene.miary.persistence;

import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

@ParseClassName("Feedback")
public class Feedback extends ParseObject {

    private static final String KEY_EMAIL = "email";
    private static final String KEY_TEXT = "text";
    private static final String KEY_INSTALLATION = "installation";

    private static final ParseACL ACL = new ParseACL();

    static {
        ACL.setPublicReadAccess(false);
        ACL.setPublicWriteAccess(false);
    }

    public Feedback() {
        setACL(ACL);
    }

    public Feedback setEmail(final String email) {
        put(KEY_EMAIL, email);
        return this;
    }

    public Feedback setText(final String text) {
        put(KEY_TEXT, text);
        return this;
    }

    public Feedback setInstallation(final ParseInstallation installation) {
        put(KEY_INSTALLATION, installation);
        return this;
    }
}
