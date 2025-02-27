package icu.congee.uuid;

import java.util.Locale;

public abstract class UUIDBase {
    public static final long VARIANT_MASK = 0xC000000000000000L; // variant bits at bit 64 and 65
    public static final long VARIANT = 0x8000000000000000L;      // variant set to 10xx

    public static final long VERSION_MASK = 0xF0000000;         // version bits at bits 48-51

    public static final long CLOCK_SEQ_MASK = 0x3FFF000000000000L;    // clock_seq bits 66-79
    public static final int CLOCK_SEQ_BITS = 14;

    public static final long NODE_MASK = 0x0000FFFFFFFFFFFFL;   // node bits 80-127
    public static final int NODE_BITS = 48;

    protected long timestamp;
    protected long clockSeq;
    protected long node;

    protected UUIDBase(long timestamp, long clockSeq, long node) {
        this.timestamp = timestamp;
        this.clockSeq = clockSeq;
        this.node = node;
    }

    public abstract long getVersion();

    public String toString() {
        long mostSigBits = ((timestamp << 16) | ((version() & 0x0f) << 12) | (clockSeq >> 2));
        long leastSigBits = ((clockSeq << 62) | node);

        return String.format(
            Locale.US,
            "%08x-%04x-%04x-%04x-%012x",
            (mostSigBits >> 32),
            (mostSigBits >> 16) & 0xFFFF,
            (mostSigBits & 0xFFFF),
            (leastSigBits >> 48) & 0xFFFF,
            leastSigBits & 0xFFFFFFFFFFFFL
        );
    }

    private int version() {
        return (int) ((this.timestamp >> 48) & 0x0F);
    }

    public static byte[] toBytes(String uuid) {
        String[] parts = uuid.split("-");
        byte[] bytes = new byte[16];

        int index = 0;
        for (String part : parts) {
            int value = Integer.parseInt(part, 16);
            if (index == 0) {
                for (int i = 0; i < 4; i++) {
                    bytes[index + i] = (byte) (value >>> ((3 - i) * 8));
                }
                index +=4;
            } else if (index ==4 || index ==6 || index ==8) {
                for (int i=0; i<2; i++) {
                    bytes[index +i] = (byte) (value >>> ((1-i)*8));
                }
                index +=2;
            } else {
                for (int i=0; i<6; i++) {
                    bytes[index +i] = (byte) (value >>> ((5-i)*8));
                }
                index +=6;
            }
        }

        return bytes;
    }
}