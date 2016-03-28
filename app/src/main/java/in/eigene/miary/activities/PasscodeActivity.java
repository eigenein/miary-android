package in.eigene.miary.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import in.eigene.miary.R;
import in.eigene.miary.helpers.PreferenceHelper;
import in.eigene.miary.helpers.TextWatcher;
import in.eigene.miary.helpers.Themes;
import in.eigene.miary.helpers.Tracking;

/**
 * Asks for passcode.
 */
public class PasscodeActivity extends AppCompatActivity {

    private static final String EXTRA_INTENT = "intent";

    public static void start(final Context context, final Intent intent) {
        context.startActivity(new Intent().setClass(context, PasscodeActivity.class).putExtra(EXTRA_INTENT, intent));
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        setTheme(Themes.getThemeResourceId(PreferenceHelper.getCurrentThemeName(this)));

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_passcode);

        setSupportActionBar((android.support.v7.widget.Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        final String truePasscode = PreferenceHelper.get(this).getString(PreferenceHelper.KEY_PASSCODE, null);
        assert truePasscode != null;

        final EditText passcodeEditText = (EditText)findViewById(R.id.passcode_edit_text);
        passcodeEditText.setTypeface(Typeface.DEFAULT);
        passcodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(final Editable s) {
                final String passcode = passcodeEditText.getText().toString();
                if (passcode.length() != truePasscode.length()) {
                    return;
                }
                if (passcode.equals(truePasscode)) {
                    finish();
                    BaseActivity.refreshLastActivityTime();
                    startActivity(getIntent().<Intent>getParcelableExtra(EXTRA_INTENT));
                    Tracking.enterCorrectPin();
                } else {
                    Toast.makeText(PasscodeActivity.this, R.string.passcode_incorrect, Toast.LENGTH_SHORT).show();
                    passcodeEditText.setText("");
                    Tracking.enterIncorrectPin();
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
