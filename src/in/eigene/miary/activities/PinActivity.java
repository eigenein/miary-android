package in.eigene.miary.activities;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.text.*;
import android.widget.*;
import com.parse.*;
import in.eigene.miary.R;
import in.eigene.miary.core.managers.*;
import in.eigene.miary.helpers.TextWatcher;

public class PinActivity extends Activity {

    private static final String EXTRA_INTENT = "intent";

    private Intent intent;

    public static void start(final Context context, final Intent intent) {
        context.startActivity(new Intent().setClass(context, PinActivity.class).putExtra(EXTRA_INTENT, intent));
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pin);
        intent = getIntent().getParcelableExtra(EXTRA_INTENT);

        final EditText pinEditText = (EditText)findViewById(R.id.pin_edit_text);
        pinEditText.setTypeface(Typeface.DEFAULT);
        pinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(final Editable s) {
                final String pin = pinEditText.getText().toString();
                if (pin.length() != 4) {
                    return;
                }
                if (PinManager.check(PinActivity.this, pin)) {
                    finish();
                    BaseActivity.refreshLastActivityTime();
                    startActivity(intent);
                    ParseAnalytics.trackEventInBackground("pinCorrect");
                } else {
                    Toast.makeText(PinActivity.this, R.string.pin_incorrect, Toast.LENGTH_SHORT).show();
                    pinEditText.setText("");
                    ParseAnalytics.trackEventInBackground("pinIncorrect");
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseActivity.resetLastActivityTime();
    }
}
