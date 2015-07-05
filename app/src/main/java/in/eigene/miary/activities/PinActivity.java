package in.eigene.miary.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseAnalytics;

import in.eigene.miary.R;
import in.eigene.miary.managers.PinManager;
import in.eigene.miary.helpers.TextWatcher;

/**
 * Asks for passcode.
 */
public class PinActivity extends ActionBarActivity {

    private static final String EXTRA_INTENT = "intent";

    public static void start(final Context context, final Intent intent) {
        context.startActivity(new Intent().setClass(context, PinActivity.class).putExtra(EXTRA_INTENT, intent));
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pin);

        setSupportActionBar((android.support.v7.widget.Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

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
                    startActivity(getIntent().<Intent>getParcelableExtra(EXTRA_INTENT));
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
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseActivity.resetLastActivityTime();
    }
}
