package in.eigene.miary.core;

import android.content.*;
import android.net.wifi.*;
import android.util.*;

import java.util.*;

/**
 * WifiManager wrapper.
 */
public class SimpleWifiConfigurationReader {

    private static final String TAG = SimpleWifiConfigurationReader.class.getName();

    public final List<SimpleWifiConfiguration> configurations = new ArrayList<SimpleWifiConfiguration>();

    public void read(final Context context) {
        Log.i(TAG, "Read configured networks…");
        final WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        final List<WifiConfiguration> configuredNetworks = wifi.getConfiguredNetworks();
        Log.d(TAG, "Configured networks: " + configuredNetworks.size());

        for (final android.net.wifi.WifiConfiguration configuration: configuredNetworks) {

            if (configuration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
                // WPA encryption.
                add(configuration.SSID, WifiEncryptionType.WPA, configuration.preSharedKey);
            } else if (configuration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE)) {
                final String wepKey = getWepKey(configuration.wepKeys);
                if (wepKey != null) {
                    // WEP encryption.
                    add(configuration.SSID, WifiEncryptionType.WEP, wepKey);
                } else {
                    // No encryption.
                    add(configuration.SSID, WifiEncryptionType.NO_PASS, null);
                }
            } else {
                // Unsupported encryption.
                Log.w(TAG, "Skipped configuration: " + configuration);
            }
        }

        Log.i(TAG, "Finished. Added: " + configurations.size());
    }

    /**
     * Gets the first non-null WEP key.
     */
    private String getWepKey(final String[] wepKeys) {
        for (final String wepKey: wepKeys) {
            if (wepKey != null) {
                return wepKey;
            }
        }
        return null;
    }

    private void add(
            final String ssid,
            final WifiEncryptionType encryptionType,
            final String password) {
        final SimpleWifiConfiguration simpleConfiguration = new SimpleWifiConfiguration(
                ssid, encryptionType, password);
        configurations.add(simpleConfiguration);
        Log.d(TAG, "Added: " + simpleConfiguration.getQrCodeData(true));
    }
}
