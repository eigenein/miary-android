package in.eigene.miary.fragments;

import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import in.eigene.miary.*;

public class ContentFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_fragment, container, false);
    }
}
