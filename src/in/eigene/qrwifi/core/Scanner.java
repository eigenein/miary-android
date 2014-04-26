package in.eigene.qrwifi.core;

import android.util.*;
import in.eigene.qrwifi.core.interfaces.*;
import in.eigene.qrwifi.exceptions.*;

import java.net.*;
import java.util.*;

public class Scanner {

    private static final String LOG_TAG = Scanner.class.getName();

    private final DeviceFoundListener listener;
    private final List<InetScanner> inetScanners = new ArrayList<InetScanner>();

    public Scanner(final DeviceFoundListener listener) {
        this.listener = listener;
    }

    public void prepare() throws InternalException {
        Log.i(LOG_TAG, "Prepare.");
        final List<NetworkInterface> interfaces;
        try {
            interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        } catch (final SocketException e) {
            throw new InternalException("Failed to get network interface list.", e);
        }
        inetScanners.clear();
        for (final NetworkInterface anInterface: interfaces) {
            boolean isUp;
            try {
                isUp = anInterface.isUp();
            } catch (final SocketException e) {
                Log.w(LOG_TAG, e);
                continue;
            }
            Log.d(LOG_TAG, "Interface: " + anInterface);
            if (isUp) {
                prepareInterface(anInterface);
            } else {
                Log.d(LOG_TAG, "Interface is down.");
            }
        }
    }

    private void prepareInterface(final NetworkInterface anInterface) {
        final List<InterfaceAddress> interfaceAddresses = anInterface.getInterfaceAddresses();
        for (final InterfaceAddress interfaceAddress: interfaceAddresses) {
            final InetScanner.Builder inetScannerBuilder = InetScanner.getByAddress(interfaceAddress);
            if (inetScannerBuilder != null) {
                final InetScanner scanner = inetScannerBuilder.build(listener);
                Log.d(LOG_TAG, "Scanner: ");
                inetScanners.add(scanner);
            }
        }
    }

    public void run() {
        for (final InetScanner inetScanner: inetScanners) {
            Log.i(LOG_TAG, "Run scanner: " + inetScanner);
            inetScanner.run();
        }
        Log.i(LOG_TAG, "Finished.");
    }
}
