package in.eigene.miary.activities;

import android.content.*;
import android.os.*;
import android.view.*;
import in.eigene.miary.*;

public class FeedbackActivity extends FullscreenDialogActivity {

    public static void start(final Context context) {
        context.startActivity(new Intent().setClass(context, FeedbackActivity.class));
    }

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
}
