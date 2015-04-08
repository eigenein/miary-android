package in.eigene.miary.fragments.base;

import android.app.Fragment;
import android.os.Bundle;

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    protected void invalidateOptionsMenu() {
        getActivity().invalidateOptionsMenu();
    }
}
