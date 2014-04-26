package in.eigene.qrwifi.core;

import android.util.*;
import in.eigene.qrwifi.common.*;
import in.eigene.qrwifi.core.interfaces.*;
import in.eigene.qrwifi.helpers.*;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class InetScanner {

    private static final String LOG_TAG = InetScanner.class.getName();
    private static final int PING_TIMEOUT = 1;
    private static final int QUEUE_SIZE = 128;

    public static interface Builder {

        InetScanner build(final DeviceFoundListener listener);
    }

    public static Builder getByAddress(final InterfaceAddress interfaceAddress) {
        Log.i(LOG_TAG, "Get by address: " + interfaceAddress);

        final InetAddress inetAddress = interfaceAddress.getAddress();
        final int prefixLength = interfaceAddress.getNetworkPrefixLength();

        if (inetAddress.isLoopbackAddress()) {
            Log.d(LOG_TAG, "Skip loopback address.");
            return null;
        }

        if (inetAddress instanceof Inet4Address) {
            return new Inet4Scanner.Builder((Inet4Address)inetAddress, prefixLength);
        }

        Log.w(LOG_TAG, "No scanner implementation found.");
        return null;
    }

    protected final DeviceFoundListener listener;

    public InetScanner(final DeviceFoundListener listener) {
        this.listener = listener;
    }

    public void run() {
        final BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<Runnable>(QUEUE_SIZE);
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(
                QUEUE_SIZE, QUEUE_SIZE, PING_TIMEOUT, TimeUnit.SECONDS, workingQueue, new ThreadPoolExecutor.CallerRunsPolicy());
        // Enqueue addresses.
        final Enumeration<InetAddress> addressEnumeration = getAddressEnumeration();
        while (addressEnumeration.hasMoreElements()) {
            final InetAddress address = addressEnumeration.nextElement();
            executor.execute(getReachableRunnable(address));
        }
        // Wait for completion.
        Log.d(LOG_TAG, "Await termination.");
        ThreadPoolHelper.awaitTermination(executor);
        Log.i(LOG_TAG, "Finished.");
    }

    protected abstract Enumeration<InetAddress> getAddressEnumeration();

    private Runnable getReachableRunnable(final InetAddress address) {
        return new Runnable() {

            @Override
            public void run() {
                Log.d(LOG_TAG, "Address: " + address);
                if (PingHelper.ping(address, PING_TIMEOUT)) {
                    Log.i(LOG_TAG, "Reachable: " + address);
                    listener.onFound(new Device());
                }
            }
        };
    }
}
