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
package icu.congee.id.generator.flexid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 独创的 FlexID 算法（简单、好用）:
 * <p>
 * 特点：
 * 1、保证 id 生成的顺序为时间顺序，越往后生成的 ID 值越大；
 * 2、运行时，单台机器并发量在每秒钟 10w 以内；
 * 3、运行时，无视时间回拨；
 * 4、最大支持 99 台机器；
 * 5、够用大概 300 年左右的时间；
 * <p>
 * 缺点：
 * 1、每台机器允许最大的并发量为 10w/s。
 * 2、出现时间回拨，重启机器时，在时间回拨未恢复的情况下，可能出现 id 重复。
 * <p>
 * ID组成：时间（7+）| 毫秒内的时间自增 （00~99：2）| 机器ID（00 ~ 99：2）| 随机数（00~99：2）用于分库分表时，通过 id
 * 取模，保证分布均衡。
 */
public class FlexIDKeyGenerator implements IdGenerator {

    private static final long INITIAL_TIMESTAMP = 1680411660000L;
    private static final long MAX_CLOCK_SEQ = 99;

    private long lastTimeMillis = 0;// 最后一次生成 ID 的时间
    private long clockSeq = 0; // 时间序列
    private long workId = 1; // 机器 ID

    public FlexIDKeyGenerator() {
    }

    public FlexIDKeyGenerator(long workId) {
        this.workId = workId;
    }

    private synchronized long nextId() {

        // 当前时间
        long currentTimeMillis = System.currentTimeMillis();

        if (currentTimeMillis == lastTimeMillis) {
            clockSeq++;
            if (clockSeq > MAX_CLOCK_SEQ) {
                clockSeq = 0;
                currentTimeMillis++;
            }
        }

        // 出现时间回拨
        else if (currentTimeMillis < lastTimeMillis) {
            currentTimeMillis = lastTimeMillis;
            clockSeq++;

            if (clockSeq > MAX_CLOCK_SEQ) {
                clockSeq = 0;
                currentTimeMillis++;
            }
        } else {
            clockSeq = 0;
        }

        lastTimeMillis = currentTimeMillis;

        long diffTimeMillis = currentTimeMillis - INITIAL_TIMESTAMP;

        // ID组成：时间（7+）| 毫秒内的时间自增 （00~99：2）| 机器ID（00 ~ 99：2）| 随机数（00~99：2）
        return diffTimeMillis * 1000000 + clockSeq * 10000 + workId * 100 + getRandomInt();
    }

    private int getRandomInt() {
        return ThreadLocalRandom.current().nextInt(100);
    }

    @Override
    public Long generate() {
        return nextId();
    }

    @Override
    public IdType idType() {
        return IdType.FlexId;
    }
}
