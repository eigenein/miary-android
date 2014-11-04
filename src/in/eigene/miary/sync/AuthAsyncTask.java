package in.eigene.miary.sync;

import android.os.*;
import com.parse.*;
import in.eigene.miary.helpers.lang.*;

public class AuthAsyncTask extends AsyncTask<Credentials, Void, String> {

    private final Consumer<String> authTokenConsumer;

    public AuthAsyncTask(final Consumer<String> authTokenConsumer) {
        this.authTokenConsumer = authTokenConsumer;
    }

    @Override
    protected String doInBackground(final Credentials... params) {
        final Credentials credentials = params[0];

        try {
            if (!credentials.getSignUp()) {
                return ParseUser.logIn(credentials.getEmail(), credentials.getPassword()).getSessionToken();
            } else {
                final ParseUser user = new ParseUser();
                user.setUsername(credentials.getEmail());
                user.setPassword(credentials.getPassword());
                user.signUp();
                return user.getSessionToken();
            }
        } catch (final ParseException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(final String authToken) {
        authTokenConsumer.accept(authToken);
    }
}
