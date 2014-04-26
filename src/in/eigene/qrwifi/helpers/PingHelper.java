package in.eigene.qrwifi.helpers;

import android.util.*;

import java.io.*;
import java.net.*;

public class PingHelper {

    private static final String LOG_TAG = PingHelper.class.getSimpleName();
    private static final String EXECUTABLE_V4 = "/system/bin/ping";

    public static boolean ping(final InetAddress address, final int timeout) {
        if (address instanceof Inet6Address) {
            throw new IllegalArgumentException("IPv6 is not supported yet.");
        }

        Process process = null;
        try {
            process = new ProcessBuilder()
                    .command(EXECUTABLE_V4, "-c", "1", "-q", "-W", Integer.toString(timeout), address.getHostAddress())
                    .redirectErrorStream(true)
                    .start();
            return process.waitFor() == 0;
        } catch (final IOException e) {
            Log.w(LOG_TAG, "I/O error.", e);
            return false;
        } catch (final InterruptedException e) {
            Log.w(LOG_TAG, "Interrupted.", e);
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}
