package in.eigene.miary.fragments.base;

import android.app.*;

public class BaseFragment extends Fragment {

    protected void invalidateOptionsMenu() {
        getActivity().invalidateOptionsMenu();
    }
}
