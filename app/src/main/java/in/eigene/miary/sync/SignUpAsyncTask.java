package in.eigene.miary.sync;

import android.content.Context;

import com.parse.ParseException;
import com.parse.ParseUser;

import in.eigene.miary.helpers.lang.Consumer;

public class SignUpAsyncTask extends AuthAsyncTask {

    public SignUpAsyncTask(final Context context, final Consumer<String> authTokenConsumer) {
        super(context, authTokenConsumer);
    }

    @Override
    protected String doAuth(final Credentials credentials) throws ParseException {
        final ParseUser user = new ParseUser();
        user.setUsername(credentials.getEmail());
        user.setEmail(credentials.getEmail());
        user.setPassword(credentials.getPassword());
        user.signUp();
        return user.getSessionToken();
    }
}
