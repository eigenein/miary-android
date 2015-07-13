package in.eigene.miary.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import in.eigene.miary.R;
import in.eigene.miary.activities.AuthenticatorActivity;

public class Authenticator extends AbstractAccountAuthenticator {

    private final Context context;
    private final Handler handler = new Handler();

    public Authenticator(final Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle editProperties(final AccountAuthenticatorResponse response, final String accountType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle addAccount(
            final AccountAuthenticatorResponse response,
            final String accountType,
            final String authTokenType,
            final String[] requiredFeatures,
            final Bundle options
    ) throws NetworkErrorException {
        final Bundle bundle = new Bundle();
        // Check account existence.
        final Account[] accounts = AccountManager.get(context).getAccountsByType(SyncAdapter.ACCOUNT_TYPE);
        if (accounts.length != 0) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, R.string.toast_only_one_supported, Toast.LENGTH_SHORT).show();
                }
            });
            bundle.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_UNSUPPORTED_OPERATION);
            bundle.putString(AccountManager.KEY_ERROR_MESSAGE, context.getString(R.string.toast_only_one_supported));
            return bundle;
        }
        // Create authenticator activity intent.
        final Intent intent = new Intent(context, AuthenticatorActivity.class)
                .putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType)
                .putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        // Return result bundle.
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        if (options != null) {
            bundle.putAll(options);
        }
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(
            final AccountAuthenticatorResponse response,
            final Account account,
            final Bundle options
    ) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(
            final AccountAuthenticatorResponse response,
            final Account account,
            final String authTokenType,
            final Bundle options
    ) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAuthTokenLabel(final String authTokenType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle updateCredentials(
            final AccountAuthenticatorResponse response,
            final Account account,
            final String authTokenType,
            final Bundle options
    ) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(
            final AccountAuthenticatorResponse response,
            final Account account,
            final String[] features
    ) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }
}
