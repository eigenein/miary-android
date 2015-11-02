package in.eigene.miary.activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import in.eigene.miary.R;
import in.eigene.miary.helpers.lang.Consumer;
import in.eigene.miary.sync.Credentials;
import in.eigene.miary.sync.SignInAsyncTask;
import in.eigene.miary.sync.SignUpAsyncTask;
import in.eigene.miary.sync.SyncAdapter;

/**
 * Authentication activity.
 */
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
                    credentials = new Credentials(getEmail(), getPassword());
                    new SignInAsyncTask(this, this).execute(credentials);
                }
                return true;
            case R.id.menu_item_auth_sign_up:
                if (validate()) {
                    credentials = new Credentials(getEmail(), getPassword());
                    new SignUpAsyncTask(this, this).execute(credentials);
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
                accountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED, "cancelled");
            }
            accountAuthenticatorResponse = null;
        }
        super.finish();
    }

    @Override
    public void accept(final String authToken) {
        final AccountManager accountManager = AccountManager.get(this);
        final Bundle result = new Bundle();

        if (TextUtils.isEmpty(authToken)) {
            result.putString(AccountManager.KEY_ERROR_MESSAGE, getString(R.string.account_auth_failed));
            Toast.makeText(this, R.string.account_auth_failed, Toast.LENGTH_LONG).show();
            return;
        }

        final Account account = new Account(credentials.getEmail(), SyncAdapter.ACCOUNT_TYPE);
        // Enable automatic syncing.
        ContentResolver.setSyncAutomatically(account, SyncAdapter.AUTHORITY, true);
        ContentResolver.setIsSyncable(account, SyncAdapter.AUTHORITY, 1);
        ContentResolver.addPeriodicSync(account, SyncAdapter.AUTHORITY, Bundle.EMPTY, 1800L);
        accountManager.addAccountExplicitly(account, credentials.getPassword(), Bundle.EMPTY);
        // Add account.
        accountManager.setAuthToken(account, account.type, authToken);
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        // Finish.
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
