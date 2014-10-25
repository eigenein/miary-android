package in.eigene.miary.sync;

import android.accounts.*;
import android.content.*;
import android.os.*;

public class Authenticator extends AbstractAccountAuthenticator {

    public Authenticator(final Context context) {
        super(context);
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
        return null;
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
