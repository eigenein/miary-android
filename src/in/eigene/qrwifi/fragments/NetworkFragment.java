package in.eigene.qrwifi.fragments;

import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import in.eigene.qrwifi.*;
import in.eigene.qrwifi.common.*;
import in.eigene.qrwifi.core.*;
import in.eigene.qrwifi.core.interfaces.*;
import in.eigene.qrwifi.exceptions.*;
import in.eigene.qrwifi.helpers.*;

public class NetworkFragment extends Fragment {

    private MenuItem refreshMenuItem;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.network, container, false);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // TODO: load network from the bundle.
        // TODO: get current network if there is no any network in the bundle.
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.network, menu);
        refreshMenuItem = menu.findItem(R.id.menu_item_network_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_network_refresh:
                new RefreshAsyncTask().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class RefreshAsyncTask
            extends AsyncTask<Void, Device, Void>
            implements DeviceFoundListener {

        private final String LOG_TAG = RefreshAsyncTask.class.getName();

        private Scanner scanner = null;

        @Override
        protected void onPreExecute() {
            RefreshAnimation.start(refreshMenuItem);
            try {
                scanner = new Scanner(this);
                scanner.prepare();
            } catch (final InternalException e) {
                Log.e(LOG_TAG, "Prepare failed.", e);
                scanner = null;
            }
        }

        @Override
        protected Void doInBackground(final Void... voids) {
            if (scanner != null) {
                scanner.run();
            } else {
                Log.w(LOG_TAG, "Do nothing.");
            }
            return null;
        }

        @Override
        public void onFound(final Device device) {
            publishProgress(device);
        }

        @Override
        protected void onProgressUpdate(final Device... values) {
            // TODO.
        }

        @Override
        protected void onPostExecute(final Void result) {
            RefreshAnimation.stop(refreshMenuItem);
        }
    }
}