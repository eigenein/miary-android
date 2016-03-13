package in.eigene.miary.activities;

import android.accounts.AccountManager;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import in.eigene.miary.R;
import in.eigene.miary.helpers.AccountHelper;
import in.eigene.miary.helpers.Tracking;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FeedbackActivity extends FullscreenDialogActivity {

    private static final String LOG_TAG = FeedbackActivity.class.getSimpleName();
    private static final String KEY = "HSXgfQu2HVpAZ0U0vSVVXZwqR3OOcvTWD7RyyYpBdBFFlx3cbCSl3XkAn7zNnq7K";

    private OkHttpClient httpClient = new OkHttpClient();

    private TextInputLayout emailInputLayout;
    private EditText emailEditText;
    private EditText textEditText;

    private String userAgent;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);
        initializeToolbar();

        emailInputLayout = (TextInputLayout)findViewById(R.id.feedback_email_layout);
        emailEditText = (EditText)findViewById(R.id.feedback_email);
        textEditText = (EditText)findViewById(R.id.feedback_text);

        try {
            userAgent = "Miary/" + getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            userAgent = "Miary/?";
        }

        final AccountManager accountManager = AccountManager.get(this);
        final String dropboxEmail =  accountManager.getUserData(
                AccountHelper.getAccount(accountManager), AccountHelper.KEY_DROPBOX_EMAIL);
        if (dropboxEmail != null) {
            emailEditText.setText(dropboxEmail);
            textEditText.requestFocus();
        }

        sendOpen();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.feedback_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_feedback_send:
                final String email = emailEditText.getText().toString();
                final String message = textEditText.getText().toString();
                if (!message.isEmpty()) {
                    if (!email.isEmpty()) {
                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            emailInputLayout.setErrorEnabled(true);
                            emailInputLayout.setError(getString(R.string.error_incorrect_email));
                            return true;
                        }
                    }
                    sendFeedback(message, email);
                    Toast.makeText(this, R.string.toast_thank_you, Toast.LENGTH_SHORT).show();
                }
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendOpen() {
        Log.i(LOG_TAG, "sendOpen");
        final Request request = new Request.Builder()
                .url("https://doorbell.io/api/applications/3286/open?key=" + KEY)
                .addHeader("User-Agent", userAgent)
                .post(RequestBody.create(null, new byte[0]))
                .build();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response response = httpClient.newCall(request).execute();
                    Log.i(LOG_TAG, "Open: " + response.code());
                } catch (final IOException e) {
                    Tracking.error("Failed to track feedback open.", e);
                }
            }
        });
    }

    private void sendFeedback(final String text, final String email) {
        final RequestBody body = new FormBody.Builder()
                .add("message", text)
                .add("email", email)
                .build();
        final Request request = new Request.Builder()
                .url("https://doorbell.io/api/applications/3286/submit?key=" + KEY)
                .addHeader("User-Agent", userAgent)
                .post(body)
                .build();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response response = httpClient.newCall(request).execute();
                    Log.i(LOG_TAG, "Submit: " + response.code());
                } catch (IOException e) {
                    Tracking.error("Failed to submit feedback.", e);
                }
            }
        });
    }
}
