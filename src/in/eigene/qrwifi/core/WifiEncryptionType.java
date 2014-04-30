package in.eigene.qrwifi.core;

public enum WifiEncryptionType {

    NO_PASS(0, null),
    WEP(1, "WEP"),
    WPA(2, "WPA");

    private final int index;
    private final String name;

    private WifiEncryptionType(final int intValue, final String stringValue) {
        this.index = intValue;
        this.name = stringValue;
    }

    public String getName() {
        return name;
    }
}
