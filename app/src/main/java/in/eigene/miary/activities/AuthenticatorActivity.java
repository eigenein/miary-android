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

public class AuthenticatorActivity extends AccountAuthenticatorActivity implements Consumer<String> {

    private EditText emailEditText;

    private EditText passwordEditText;

    private Credentials credentials;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_authenticator);

        emailEditText = (EditText)findViewById(R.id.auth_email_edit_text);
        passwordEditText = (EditText)findViewById(R.id.auth_password_edit_text);

        passwordEditText.setTypeface(Typeface.DEFAULT);

        findViewById(R.id.auth_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (validate()) {
                    // TODO: disable controls.
                    // TODO: show progress dialog.
                    credentials = new Credentials(getEmail(), getPassword(), false);
                    new AuthAsyncTask(AuthenticatorActivity.this).execute(credentials);
                }
            }
        });

        findViewById(R.id.auth_sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (validate()) {
                    // TODO: disable controls.
                    // TODO: show progress dialog.
                    credentials = new Credentials(getEmail(), getPassword(), true);
                    new AuthAsyncTask(AuthenticatorActivity.this).execute(credentials);
                }
            }
        });
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

    @Override
    public void accept(final String authToken) {
        final AccountManager accountManager = AccountManager.get(this);
        final Bundle result = new Bundle();

        if (!Util.isNullOrEmpty(authToken)) {
            final Account account = new Account(credentials.getEmail(), "miary.eigene.in");
            accountManager.addAccountExplicitly(account, credentials.getPassword(), new Bundle());
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            accountManager.setAuthToken(account, account.type, authToken);
            Toast.makeText(this, R.string.account_auth_success, Toast.LENGTH_LONG).show();
        } else {
            result.putString(AccountManager.KEY_ERROR_MESSAGE, getString(R.string.account_auth_failed));
            Toast.makeText(this, R.string.account_auth_failed, Toast.LENGTH_LONG).show();
        }

        setAccountAuthenticatorResult(result);
        setResult(RESULT_OK);
        finish();
    }
}