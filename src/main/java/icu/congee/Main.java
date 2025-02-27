package icu.congee;

import cn.hutool.log.Log;
import icu.congee.uuid.UUIDv1Generator;

import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        Log log = Log.get();
//        String uuid = UUIDv1Generator.getInstance().generate();
//        log.info(uuid);

        for(int i = 0; i < 100; i++) {
            Instant timestamp = Instant.now();
            log.info("timestamp = {}",timestamp);
        }
    }
}
