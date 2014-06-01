package in.eigene.miary;

import in.eigene.miary.helpers.*;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        ParseHelper.initialize(this);
    }
}
