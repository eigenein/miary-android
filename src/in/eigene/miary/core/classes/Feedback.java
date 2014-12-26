package in.eigene.miary.core.classes;

import com.parse.*;

@ParseClassName("Feedback")
public class Feedback extends ParseObject {

    private static final String KEY_EMAIL = "email";
    private static final String KEY_TEXT = "text";

    public Feedback() {
        // Do nothing.
    }

    public Feedback setEmail(final String email) {
        put(KEY_EMAIL, email);
        return this;
    }

    public Feedback setText(final String text) {
        put(KEY_TEXT, text);
        return this;
    }
}
