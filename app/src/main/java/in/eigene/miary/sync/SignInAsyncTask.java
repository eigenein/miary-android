package in.eigene.miary.sync;

import android.content.*;
import com.parse.*;
import in.eigene.miary.helpers.lang.*;

public class SignInAsyncTask extends AuthAsyncTask {

    public SignInAsyncTask(final Context context, final Consumer<String> authTokenConsumer) {
        super(context, authTokenConsumer);
    }

    @Override
    protected String doAuth(final Credentials credentials) throws ParseException {
        return ParseUser.logIn(credentials.getEmail(), credentials.getPassword()).getSessionToken();
    }
}
