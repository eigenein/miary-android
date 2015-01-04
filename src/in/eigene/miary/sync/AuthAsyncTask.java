package in.eigene.miary.sync;

import android.os.*;
import com.parse.*;
import in.eigene.miary.helpers.lang.*;

public abstract class AuthAsyncTask extends AsyncTask<Credentials, Void, String> {

    private final Consumer<String> authTokenConsumer;

    public AuthAsyncTask(final Consumer<String> authTokenConsumer) {
        this.authTokenConsumer = authTokenConsumer;
    }

    @Override
    protected String doInBackground(final Credentials... params) {
        try {
            return doAuth(params[0]);
        } catch (final ParseException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(final String authToken) {
        authTokenConsumer.accept(authToken);
    }

    /**
     * Authenticates user and returns authentication token.
     */
    protected abstract String doAuth(final Credentials credentials) throws ParseException;
}
