package in.eigene.qrwifi.fragments;

import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import in.eigene.qrwifi.*;

public class KnownNetworksFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.known_networks_fragment, container, false);
    }
}
