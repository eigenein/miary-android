package in.eigene.miary;

import com.parse.*;
import in.eigene.miary.helpers.*;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        ParseHelper.initialize(this);

        if (ParseUser.getCurrentUser() != null) {
            ParseACL.setDefaultACL(new ParseACL(), true);
        }
    }
}
