package icu.congee;

import cn.hutool.log.Log;
import icu.congee.LexicalUUID.LexicalUUID;
import icu.congee.LexicalUUID.MicrosecondEpochClock;
import icu.congee.cuid.CUID;
import icu.congee.ulid.ULID;


public class Main {
    private static final Log log = Log.get();

    public static void main(String[] args) {

        /**
         * import de.huxhorn.sulky.ulid.ULID;
         *
         * ULID ulid = new ULID();
         */
        ULID ulid = new ULID();
        log.info("ulid={}", ulid.nextULID());
        for(int i = 0; i < 10; i++) {
            LexicalUUID lexicalUUID = LexicalUUID.create(MicrosecondEpochClock.getInstance());
            log.info("lexicalUUID={}", lexicalUUID);
        }

        for(int i = 0; i < 10; i++) {
            log.info("cuid1={}", CUID.randomCUID1());
        }
        for(int i = 0; i < 10; i++) {
            log.info("cuid2={}", CUID.randomCUID2());
        }
    }
}
