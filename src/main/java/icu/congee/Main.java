package icu.congee;

import cn.hutool.log.Log;
import icu.congee.uuid.UUIDv1Generator;
import icu.congee.uuid.UUIDv7Generator;

import java.time.Instant;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        Log log = Log.get();
        UUID uuid = UUIDv7Generator.generate();
        log.info(uuid.toString());
        log.info(String.valueOf(uuid.version()));
        log.info(String.valueOf(uuid.variant()));

//        for(int i = 0; i < 100; i++) {
//            Instant timestamp = Instant.now();
//            log.info("timestamp = {}",timestamp);
//        }
    }
}
