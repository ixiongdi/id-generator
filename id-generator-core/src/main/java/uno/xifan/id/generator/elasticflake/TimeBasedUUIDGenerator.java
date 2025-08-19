/*
 * MIT License
 *
 * Copyright (c) 2025 ixiongdi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 */

package uno.xifan.id.generator.elasticflake;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * These are essentially flake ids
 * (http://boundary.com/blog/2012/01/12/flake-a-decentralized-k-ordered-unique-id-generator-in-erlang)
 * but
 * we use 6 (not 8) bytes for timestamp, and use 3 (not 2) bytes for sequence
 * number.
 */

public class TimeBasedUUIDGenerator implements UUIDGenerator {

    // We only use bottom 3 bytes for the sequence number. Paranoia: init with
    // random int so that if JVM/OS/machine goes down, clock slips
    // backwards, and JVM comes back up, we are less likely to be on the same
    // sequenceNumber at the same time:
    private final AtomicInteger sequenceNumber = new AtomicInteger(SecureRandomHolder.INSTANCE.nextInt());

    // Used to ensure clock moves forward:
    private long lastTimestamp;

    private static final byte[] secureMungedAddress = MacAddressProvider.getSecureMungedAddress();

    static {
        assert secureMungedAddress.length == 6;
    }

    /**
     * Puts the lower numberOfLongBytes from l into the array, starting index pos.
     */
    private static void putLong(byte[] array, long l, int pos, int numberOfLongBytes) {
        for (int i = 0; i < numberOfLongBytes; ++i) {
            array[pos + numberOfLongBytes - i - 1] = (byte) (l >>> (i * 8));
        }
    }

    @Override
    public String getBase64UUID() {
        final int sequenceId = sequenceNumber.incrementAndGet() & 0xffffff;
        long timestamp = System.currentTimeMillis();

        synchronized (this) {
            // Don't let timestamp go backwards, at least "on our watch" (while this JVM is
            // running). We are still vulnerable if we are
            // shut down, clock goes backwards, and we restart... for this we randomize the
            // sequenceNumber on init to decrease chance of
            // collision:
            timestamp = Math.max(lastTimestamp, timestamp);

            if (sequenceId == 0) {
                // Always force the clock to increment whenever sequence number is 0, in case we
                // have a long time-slip backwards:
                timestamp++;
            }

            lastTimestamp = timestamp;
        }

        final byte[] uuidBytes = new byte[15];

        // Only use lower 6 bytes of the timestamp (this will suffice beyond the year
        // 10000):
        putLong(uuidBytes, timestamp, 0, 6);

        // MAC address adds 6 bytes:
        System.arraycopy(secureMungedAddress, 0, uuidBytes, 6, secureMungedAddress.length);

        // Sequence number adds 3 bytes:
        putLong(uuidBytes, sequenceId, 12, 3);

        assert 9 + secureMungedAddress.length == uuidBytes.length;

        byte[] encoded;
        try {
            encoded = Base64.encodeBytesToBytes(uuidBytes, 0, uuidBytes.length, Base64.URL_SAFE);
        } catch (IOException e) {
            throw new RuntimeException("should not be thrown", e);
        }

        // We are a multiple of 3 bytes so we should not see any padding:
        assert encoded[encoded.length - 1] != '=';
        return new String(encoded, 0, encoded.length, Base64.PREFERRED_ENCODING);
    }
}