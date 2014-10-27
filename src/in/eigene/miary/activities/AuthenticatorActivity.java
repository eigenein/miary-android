package in.eigene.miary.activities;

import android.accounts.*;
import android.graphics.*;
import android.os.*;
import android.widget.*;
import in.eigene.miary.*;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_authenticator);
        ((EditText)findViewById(R.id.auth_password_edit_text)).setTypeface(Typeface.DEFAULT);
    }
}