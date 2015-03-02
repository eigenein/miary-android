package in.eigene.miary.sync;

import android.app.*;
import android.content.*;
import android.os.*;

public class AuthenticatorService extends Service {

    private Authenticator authenticator;

    @Override
    public void onCreate() {
        authenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return authenticator.getIBinder();
    }
}
