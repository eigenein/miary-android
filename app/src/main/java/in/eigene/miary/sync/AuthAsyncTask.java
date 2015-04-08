package in.eigene.miary.sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.parse.ParseException;

import in.eigene.miary.R;
import in.eigene.miary.helpers.lang.Consumer;

public abstract class AuthAsyncTask extends AsyncTask<Credentials, Void, String> {

    private final ProgressDialog progressDialog;
    private final Consumer<String> authTokenConsumer;

    public AuthAsyncTask(final Context context, final Consumer<String> authTokenConsumer) {
        this.authTokenConsumer = authTokenConsumer;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getString(R.string.dialog_authenticating));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();
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
        progressDialog.dismiss();
        authTokenConsumer.accept(authToken);
    }

    /**
     * Authenticates user and returns authentication token.
     */
    protected abstract String doAuth(final Credentials credentials) throws ParseException;
}
