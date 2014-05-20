package in.eigene.miary.core;

public class SimpleWifiConfiguration {

    private final String ssid;
    private final WifiEncryptionType encryptionType;
    private final String password;

    public SimpleWifiConfiguration(
            final String ssid,
            final WifiEncryptionType encryptionType,
            final String password) {
        this.ssid = ssid;
        this.encryptionType = encryptionType;
        this.password = password;
    }

    public String getQrCodeData(final boolean includePassword) {
        final StringBuilder builder = new StringBuilder("WIFI:");

        builder.append(String.format("S:%s;", ssid));

        if (encryptionType != WifiEncryptionType.NO_PASS) {
            builder.append(String.format("T:%s;", encryptionType.getName()));
            builder.append(String.format("P:%s;", includePassword ? password : "<masked>"));
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return String.format("WifiConfiguration[%s]", getQrCodeData(false));
    }
}
