package in.eigene.miary.activities;

import android.os.*;
import android.view.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.core.classes.*;

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
                    final Feedback feedback = new Feedback().setText(text);
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
