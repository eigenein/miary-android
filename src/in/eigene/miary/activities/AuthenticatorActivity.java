package in.eigene.miary.activities;

import android.accounts.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.helpers.*;
import in.eigene.miary.helpers.lang.*;
import in.eigene.miary.sync.*;

public class AuthenticatorActivity extends FullscreenDialogActivity implements Consumer<String> {

    private AccountAuthenticatorResponse accountAuthenticatorResponse = null;
    private Bundle result = null;

    private EditText emailEditText;
    private EditText passwordEditText;

    private Credentials credentials;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountAuthenticatorResponse = getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        if (accountAuthenticatorResponse != null) {
            accountAuthenticatorResponse.onRequestContinued();
        }

        setContentView(R.layout.activity_authenticator);
        initializeToolbar();

        emailEditText = (EditText)findViewById(R.id.auth_email_edit_text);
        passwordEditText = (EditText)findViewById(R.id.auth_password_edit_text);

        passwordEditText.setTypeface(Typeface.DEFAULT);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.authenticator_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_auth_sign_in:
                if (validate()) {
                    // TODO: disable controls.
                    // TODO: show progress dialog.
                    credentials = new Credentials(getEmail(), getPassword());
                    new SignInAsyncTask(AuthenticatorActivity.this).execute(credentials);
                }
                return true;
            case R.id.menu_item_auth_sign_up:
                if (validate()) {
                    // TODO: disable controls.
                    // TODO: show progress dialog.
                    credentials = new Credentials(getEmail(), getPassword());
                    new SignUpAsyncTask(AuthenticatorActivity.this).execute(credentials);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sends the result or a Constants.ERROR_CODE_CANCELED error if a result isn't present.
     */
    @Override
    public void finish() {
        if (accountAuthenticatorResponse != null) {
            // Send the result bundle back if set, otherwise send an error.
            if (result != null) {
                accountAuthenticatorResponse.onResult(result);
            } else {
                accountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED, "canceled");
            }
            accountAuthenticatorResponse = null;
        }
        super.finish();
    }

    @Override
    public void accept(final String authToken) {
        final AccountManager accountManager = AccountManager.get(this);
        final Bundle result = new Bundle();

        if (Util.isNullOrEmpty(authToken)) {
            result.putString(AccountManager.KEY_ERROR_MESSAGE, getString(R.string.account_auth_failed));
            Toast.makeText(this, R.string.account_auth_failed, Toast.LENGTH_LONG).show();
            return;
        }

        final Account account = new Account(credentials.getEmail(), "miary.eigene.in");
        accountManager.addAccountExplicitly(account, credentials.getPassword(), new Bundle());
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        accountManager.setAuthToken(account, account.type, authToken);
        Toast.makeText(this, R.string.account_auth_success, Toast.LENGTH_LONG).show();

        this.result = result;
        setResult(RESULT_OK);
        finish();
    }

    private String getEmail() {
        return emailEditText.getText().toString().trim();
    }

    private String getPassword() {
        return passwordEditText.getText().toString();
    }

    private boolean validate() {
        if (getEmail().isEmpty()) {
            Toast.makeText(this, R.string.toast_email_required, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (getPassword().isEmpty()) {
            Toast.makeText(this, R.string.toast_password_required, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
