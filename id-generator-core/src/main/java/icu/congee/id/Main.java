package icu.congee.id;

import icu.congee.id.generator.custom.TimeBasedEntropyIdGenerator;
import icu.congee.id.generator.uuid.UUIDv7Generator;
import icu.congee.id.util.IdUtil;

public class Main {

    public static void main(String[] args) {
        // 原创算法（基于时间戳的多熵源ID生成）
        TimeBasedEntropyIdGenerator timeBasedEntropyIdGenerator = new TimeBasedEntropyIdGenerator();
        for (int i = 0; i < 10; i++) {
            System.out.println(timeBasedEntropyIdGenerator.generate());
        }
        // UUID v7（基于时间戳和随机数的ID生成）
        UUIDv7Generator uuiDv7Generator = new UUIDv7Generator();
        for (int i = 0; i < 10; i++) {
            System.out.println(uuiDv7Generator.generate());
        }

        // 有一些ID生成直接封装在了IdUtil里，可以这样使用
        for (int i = 0; i < 10; i++) {
            System.out.println(IdUtil.entropy());
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(IdUtil.uuid7());
        }
    }
}
