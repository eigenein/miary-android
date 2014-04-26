package in.eigene.qrwifi.helpers;

import java.net.*;
import java.util.*;

public class Inet4AddressHelper {

    public static int pack(final Inet4Address address) {
        final byte[] parts = address.getAddress();
        return ((parts[0] << 24) & 0xFF000000) | ((parts[1] << 16) & 0xFF0000) | ((parts[2] << 8) & 0xFF00) | (parts[3] & 0xFF);
    }

    public static Inet4Address unpack(final int address)
            throws UnknownHostException {
        return (Inet4Address)Inet4Address.getByAddress(new byte[] {
                (byte)(address >> 24),
                (byte)((address >> 16) & 0xFF),
                (byte)((address >> 8) & 0xFF),
                (byte)(address & 0xFF)
        });
    }

    public static int getSubnetMask(final int prefixLength) {
        return 0xFFFFFFFF << (32 - prefixLength);
    }

    public static int getMaxAddress(final int startAddress, final int prefixLength) {
        return startAddress + (1 << (32 - prefixLength)) - 3;
    }

    public static class SubnetAddressEnumeration implements Enumeration<InetAddress> {

        private final int maxAddress;
        private int currentAddress;

        public SubnetAddressEnumeration(final Inet4Address localAddress, final int prefixLength) {
            if (prefixLength < 31) {
                final int startAddress = (pack(localAddress) & getSubnetMask(prefixLength)) + 1;
                currentAddress = startAddress;
                maxAddress = getMaxAddress(startAddress, prefixLength);
            } else {
                currentAddress = maxAddress = pack(localAddress);
            }
        }

        @Override
        public boolean hasMoreElements() {
            return currentAddress <= maxAddress;
        }

        @Override
        public InetAddress nextElement() {
            try {
                return unpack(currentAddress);
            } catch (final UnknownHostException e) {
                throw new RuntimeException("Failed to unpack address.", e);
            } finally {
                currentAddress += 1;
            }
        }
    }
}
