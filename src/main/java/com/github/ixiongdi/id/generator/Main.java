package com.github.ixiongdi.id.generator;

import cn.hutool.log.Log;

public class Main {
    private static final Log log = Log.get();

    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {
            log.info("timeBasedBusinessId={}", BestPracticeNumberIdGenerator.timeBasedBusinessId());
        }
        for (int i = 0; i < 10; i++) {
            log.info("timeBasedRandomId={}", BestPracticeNumberIdGenerator.timeBasedRandomId());
        }
    }
}
