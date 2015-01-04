package in.eigene.miary.sync;

import com.parse.*;
import in.eigene.miary.helpers.lang.*;

public class SignUpAsyncTask extends AuthAsyncTask {

    public SignUpAsyncTask(final Consumer<String> authTokenConsumer) {
        super(authTokenConsumer);
    }

    @Override
    protected String doAuth(final Credentials credentials) throws ParseException {
        final ParseUser user = new ParseUser();
        user.setUsername(credentials.getEmail());
        user.setPassword(credentials.getPassword());
        user.signUp();
        return user.getSessionToken();
    }
}
