package icu.congee;

import cn.hutool.log.Log;
import icu.congee.uuid.UUIDv1Generator;

public class Main {
    public static void main(String[] args) {
        Log log = Log.get();
        String uuid = UUIDv1Generator.getInstance().generate();
        log.info(uuid);
    }
}
