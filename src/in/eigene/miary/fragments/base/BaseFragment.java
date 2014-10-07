package in.eigene.miary.fragments.base;

import android.app.*;
import android.os.*;

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
