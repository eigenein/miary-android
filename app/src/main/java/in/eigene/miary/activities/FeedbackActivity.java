package in.eigene.miary.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseInstallation;

import in.eigene.miary.R;
import in.eigene.miary.persistence.Feedback;

public class FeedbackActivity extends FullscreenDialogActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);
        initializeToolbar();
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
                final String email = ((EditText)findViewById(R.id.feedback_email)).getText().toString();
                final String text = ((EditText)findViewById(R.id.feedback_text)).getText().toString();
                if (!text.isEmpty()) {
                    final Feedback feedback = new Feedback()
                            .setInstallation(ParseInstallation.getCurrentInstallation())
                            .setText(text);
                    if (!email.isEmpty()) {
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
