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
import java.util.Random;

class RandomBasedUUIDGenerator implements UUIDGenerator {

    /**
     * Returns a Base64 encoded version of a Version 4.0 compatible UUID
     * as defined here: http://www.ietf.org/rfc/rfc4122.txt
     */
    @Override
    public String getBase64UUID() {
        return getBase64UUID(SecureRandomHolder.INSTANCE);
    }

    /**
     * Returns a Base64 encoded version of a Version 4.0 compatible UUID
     * randomly initialized by the given {@link java.util.Random} instance
     * as defined here: http://www.ietf.org/rfc/rfc4122.txt
     */
    public String getBase64UUID(Random random) {
        final byte[] randomBytes = new byte[16];
        random.nextBytes(randomBytes);
        /*
         * Set the version to version 4 (see http://www.ietf.org/rfc/rfc4122.txt)
         * The randomly or pseudo-randomly generated version.
         * The version number is in the most significant 4 bits of the time
         * stamp (bits 4 through 7 of the time_hi_and_version field).
         */
        randomBytes[6] &= 0x0f; /* clear the 4 most significant bits for the version */
        randomBytes[6] |= 0x40; /* set the version to 0100 / 0x40 */

        /*
         * Set the variant:
         * The high field of th clock sequence multiplexed with the variant.
         * We set only the MSB of the variant
         */
        randomBytes[8] &= 0x3f; /* clear the 2 most significant bits */
        randomBytes[8] |= 0x80; /* set the variant (MSB is set) */
        try {
            byte[] encoded = Base64.encodeBytesToBytes(randomBytes, 0, randomBytes.length, Base64.URL_SAFE);
            // we know the bytes are 16, and not a multi of 3, so remove the 2 padding chars
            // that are added
            assert encoded[encoded.length - 1] == '=';
            assert encoded[encoded.length - 2] == '=';
            return new String(encoded, 0, encoded.length - 2, Base64.PREFERRED_ENCODING);
        } catch (IOException e) {
            throw new RuntimeException("should not be thrown");
        }
    }
}