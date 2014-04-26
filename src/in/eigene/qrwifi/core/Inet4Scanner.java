package in.eigene.qrwifi.core;

import in.eigene.qrwifi.core.interfaces.*;
import in.eigene.qrwifi.helpers.*;

import java.net.*;
import java.util.*;

/**
 * IPv4 scanner.
 */
public class Inet4Scanner extends InetScanner {

    private final Inet4Address address;
    private final int prefixLength;

    public Inet4Scanner(
            final DeviceFoundListener listener,
            Inet4Address address,
            final int prefixLength
    ) {
        super(listener);
        this.address = address;
        this.prefixLength = prefixLength;
    }

    @Override
    public String toString() {
        return String.format("%s[address=%s, prefix=%s]", Inet4Scanner.class.getSimpleName(), address, prefixLength);
    }

    @Override
    protected Enumeration<InetAddress> getAddressEnumeration() {
        return new Inet4AddressHelper.SubnetAddressEnumeration(address, prefixLength);
    }

    public static class Builder implements InetScanner.Builder {

        private final Inet4Address address;
        private final int prefixLength;

        public Builder(final Inet4Address address, final int prefixLength) {
            this.address = address;
            this.prefixLength = prefixLength;
        }

        @Override
        public InetScanner build(final DeviceFoundListener listener) {
            return new Inet4Scanner(listener, address, prefixLength);
        }
    }
}
