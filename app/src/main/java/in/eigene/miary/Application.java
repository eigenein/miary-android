package in.eigene.miary;

import in.eigene.miary.helpers.ParseHelper;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        ParseHelper.initialize(this);
    }
}
