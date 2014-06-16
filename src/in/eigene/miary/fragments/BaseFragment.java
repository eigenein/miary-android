package in.eigene.miary.fragments;

import android.app.*;

public class BaseFragment extends Fragment {

    protected void invalidateOptionsMenu() {
        getActivity().invalidateOptionsMenu();
    }
}
