package icu.congee.id.generator.broid;

import icu.congee.id.base.Base36Codec;
import icu.congee.id.base.Base62Codec;
import icu.congee.id.base.CrockfordBase32;
import icu.congee.id.base.HexCodec;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BroId {
    private final String name = "BroId";
    private final String desc = "Custom Id";
    private final List<Boolean> value;

    @Override
    public String toString() {
        return toBase62String();
    }

    public String toBase62String() {
        return Base62Codec.encode(BitUtils.listToByteArray(value));
    }

    public String toBase36String() {
        return Base36Codec.encode(BitUtils.listToByteArray(value));
    }

    public String toCrockfordBase32String() {
        return CrockfordBase32.encode(BitUtils.listToByteArray(value));
    }

    public String toHexString() {
        return HexCodec.encode(BitUtils.listToByteArray(value));
    }

    public Long toLong() {
        return BitUtils.listToLong(value);
    }

    public UUID toUUID() {
        // The 4-bit version field as defined by Section 4.2, set to 0b1000 (8). Occupies bits 48 through 51 of octet 6.
        value.set(48, true);
        value.set(49, false);
        value.set(50, false);
        value.set(51, false);
        // The 2-bit variant field as defined by Section 4.1, set to 0b10. Occupies bits 64 and 65 of octet 8.
        value.set(64, true);
        value.set(65, false);
        long msb = BitUtils.listToLong(value.subList(0, 64));
        long lsb = BitUtils.listToLong(value.subList(64, 128));
        return new UUID(msb, lsb);
    }
}
