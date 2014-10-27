package in.eigene.miary.sync;

import android.accounts.*;
import android.content.*;
import android.os.*;
import in.eigene.miary.activities.*;

public class Authenticator extends AbstractAccountAuthenticator {

    private final Context context;

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

        final Intent intent = new Intent(context, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
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
