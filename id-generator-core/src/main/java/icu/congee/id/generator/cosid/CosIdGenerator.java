/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// 定义包路径
package icu.congee.id.generator.cosid;

// 导入必要的类
import icu.congee.id.base.Base62Codec;
import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;

/**
 * CosId分布式ID生成器实现类
 * 采用80位二进制结构：44位时间戳 + 20位机器ID + 16位序列号
 */
public class CosIdGenerator implements IdGenerator {
    // 位分配常量定义
    private static final int TIMESTAMP_BITS = 44; // 时间戳占用44位
    private static final int MACHINE_ID_BITS = 20; // 机器ID占用20位
    private static final int SEQUENCE_BITS = 16; // 序列号占用16位

    // 计算最大值常量
    private static final long MAX_MACHINE_ID = (1L << MACHINE_ID_BITS) - 1; // 机器ID最大值（1048575）
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1; // 序列号最大值（65535）

    // 基础组件参数
    private final long epoch; // 自定义纪元时间戳
    private final long machineId; // 机器ID

    // 运行时状态变量
    private long lastTimestamp = -1L; // 上次生成ID的时间戳
    private long sequence = 0L; // 当前序列号

    // 单例实例，用于默认的ID生成
    private static final CosIdGenerator cosIdGenerator = new CosIdGenerator();

    public CosIdGenerator() {
        this.epoch = 0;
        this.machineId = 0;
    }

    /**
     * 构造函数
     * 
     * @param machineId 机器ID（0~1048575）
     * @param epoch     自定义纪元时间戳
     */
    public CosIdGenerator(long machineId, long epoch) {
        // 验证机器ID是否在有效范围内
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException("Machine ID must be between 0 and " + MAX_MACHINE_ID);
        }
        this.machineId = machineId;
        this.epoch = epoch;
    }

    public static String next() {
        return Base62Codec.encode(cosIdGenerator.generateId());
    }

    /**
     * 生成分布式ID的字节数组
     * 该方法使用synchronized确保线程安全
     * 
     * @return 10字节的ID字节数组
     */
    public synchronized byte[] generateId() {
        // 获取当前时间戳
        long currentTimestamp = getCurrentTimestamp();

        // 检查时钟回拨
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards!");
        }

        // 处理同一毫秒内的序列号
        if (currentTimestamp == lastTimestamp) {
            // 序列号递增，并与最大值进行与运算确保不超出范围
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 当序列号用尽时，等待下一毫秒
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            // 不同毫秒，序列号重置为0
            sequence = 0;
        }

        // 更新上次生成ID的时间戳
        lastTimestamp = currentTimestamp;

        // 构建并返回字节数组格式的ID
        return buildByteArray(currentTimestamp, machineId, sequence);
    }

    /**
     * 将时间戳、机器ID和序列号打包成字节数组
     * 
     * @param timestamp 时间戳（44位）
     * @param machineId 机器ID（20位）
     * @param sequence  序列号（16位）
     * @return 10字节的字节数组
     */
    private byte[] buildByteArray(long timestamp, long machineId, long sequence) {
        // 分配10字节的缓冲区（80位）
        ByteBuffer buffer = ByteBuffer.allocate(10);

        // 写入时间戳（44位，占用5.5字节）
        buffer.put((byte) (timestamp >>> 36)); // 时间戳的高8位
        buffer.put((byte) (timestamp >>> 28 & 0xFF)); // 时间戳的次高8位
        buffer.put((byte) (timestamp >>> 20 & 0xFF)); // 时间戳的中间8位
        buffer.put((byte) (timestamp >>> 12 & 0xFF)); // 时间戳的次低8位
        buffer.put((byte) (timestamp >>> 4 & 0xFF)); // 时间戳的低8位

        // 处理第6字节（时间戳最后4位 + 机器ID高4位）
        byte sixthByte = (byte) ((timestamp & 0x0F) << 4); // 时间戳最后4位左移4位
        sixthByte |= (byte) (machineId >>> 16 & 0x0F); // 合并机器ID的高4位
        buffer.put(sixthByte);

        // 写入机器ID的剩余16位（占用2字节）
        buffer.putShort((short) (machineId & 0xFFFF));

        // 写入序列号（16位，占用2字节）
        buffer.putShort((short) sequence);

        // 返回完整的字节数组
        return buffer.array();
    }

    /**
     * 等待下一毫秒
     * 
     * @param currentTimestamp 当前时间戳
     * @return 下一毫秒的时间戳
     */
    private long waitNextMillis(long currentTimestamp) {
        long now;
        do {
            now = getCurrentTimestamp();
        } while (now <= currentTimestamp); // 循环等待直到进入下一毫秒
        return now;
    }

    /**
     * 获取当前时间戳（相对于自定义纪元）
     * 
     * @return 相对时间戳
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis() - epoch;
    }

    /**
     * 主方法，用于测试ID生成器
     */
    public static void main(String[] args) {
        // 设置自定义纪元时间为2020年1月1日
        long customEpoch = Instant.parse("2020-01-01T00:00:00Z").toEpochMilli();
        // 创建ID生成器实例，使用特定的机器ID
        CosIdGenerator generator = new CosIdGenerator(0xABCDEL, customEpoch);

        // 生成并打印ID
        byte[] id = generator.generateId();
        System.out.println("字节数组: " + Arrays.toString(id));
        System.out.println("十六进制: " + bytesToHex(id));
    }

    /**
     * 将字节数组转换为十六进制字符串
     * 
     * @param bytes 要转换的字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 实现IdGenerator接口的generate方法
     * 
     * @return Base62编码的ID字符串
     */
    @Override
    public Object generate() {
        return Base62Codec.encode(cosIdGenerator.generateId());
    }

    /**
     * 实现IdGenerator接口的idType方法
     * 
     * @return ID类型为CosId
     */
    @Override
    public IdType idType() {
        return IdType.CosId;
    }
}