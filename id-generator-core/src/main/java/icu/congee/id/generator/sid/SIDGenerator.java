package icu.congee.id.generator.sid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import icu.congee.id.base.TimeUtils;

import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SIDGenerator implements IdGenerator {
    private static final Random random = ThreadLocalRandom.current();

    private static byte[] longToBytes(long value) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (value >>> 56);
        bytes[1] = (byte) (value >>> 48);
        bytes[2] = (byte) (value >>> 40);
        bytes[3] = (byte) (value >>> 32);
        bytes[4] = (byte) (value >>> 24);
        bytes[5] = (byte) (value >>> 16);
        bytes[6] = (byte) (value >>> 8);
        bytes[7] = (byte) value;
        return bytes;
    }

    @Override
    public Object generate() {
        byte[] timestamp = longToBytes(TimeUtils.getCurrentUnixNano());
        byte[] randomNum = longToBytes(random.nextLong());
        return Base64.getEncoder().encodeToString(timestamp) + "-" + Base64.getEncoder().encodeToString(randomNum);
    }

    @Override
    public IdType idType() {
        return IdType.SID;
    }
}
