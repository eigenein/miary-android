package in.eigene.miary.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseInstallation;
import com.parse.ParseUser;

import in.eigene.miary.R;
import in.eigene.miary.persistence.Feedback;

public class FeedbackActivity extends FullscreenDialogActivity {

    private TextInputLayout emailInputLayout;
    private EditText emailEditText;
    private EditText textEditText;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);
        initializeToolbar();

        emailInputLayout = (TextInputLayout)findViewById(R.id.feedback_email_layout);
        emailEditText = (EditText)findViewById(R.id.feedback_email);
        textEditText = (EditText)findViewById(R.id.feedback_text);

        final ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            emailEditText.setText(user.getUsername());
        }
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
                final String text = textEditText.getText().toString();
                if (!text.isEmpty()) {
                    final Feedback feedback = new Feedback()
                            .setInstallation(ParseInstallation.getCurrentInstallation())
                            .setText(text);
                    if (!email.isEmpty()) {
                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            emailInputLayout.setErrorEnabled(true);
                            emailInputLayout.setError(getString(R.string.error_incorrect_email));
                            return true;
                        }
                        feedback.setEmail(email);
                    }
                    feedback.saveEventually();
                    Toast.makeText(this, R.string.toast_thank_you, Toast.LENGTH_SHORT).show();
                }
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
