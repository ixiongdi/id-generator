package com.github.ixiongdi.id.generator.uuid;

import lombok.Data;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Enumeration;

@Data
public class UUIDv1 {
    private static final long START_EPOCH = 0x01b21dd213814000L;
    private static volatile short clockSequence;
    
    // 字段定义
    private BitSet timeLow;     // 32 bits (0-31)
    private BitSet timeMid;     // 16 bits (32-47)
    private BitSet timeHighVer; // 16 bits (48-63) [12位时间戳 + 4位版本]
    private BitSet varClockSeq; // 16 bits (64-79) [2位变体 + 14位时钟序列]
    private BitSet node;        // 48 bits (80-127)

    public UUIDv1() {
        long timestamp = (System.currentTimeMillis() * 10_000) + START_EPOCH;
        
        // 时间戳拆分（60位）
        this.timeLow = toBitSet(timestamp, 32, 32); // 截取32-63位（实际是低32位）
        this.timeMid = toBitSet(timestamp, 16, 16); // 截取16-31位（中间16位）
        this.timeHighVer = toBitSet(timestamp, 12, 0) // 高12位时间戳
            .get(0, 16); // 扩展到16位
        
        // 设置版本号（0b0001到第13-16位）
        setBits(timeHighVer, 12, 4, 0x1L);
        
        // 生成时钟序列（14位）
        BitSet clockSeq = generateClockSequence();
        this.varClockSeq = clockSeq.get(0, 16);
        
        // 设置变体（0b10到最高两位）
        setBits(varClockSeq, 14, 2, 0x2L);
        
        // 节点地址
        this.node = getMacAddressBits();
    }

    // BitSet操作工具
    private void setBits(BitSet bitset, int startBit, int length, long value) {
        for (int i = 0; i < length; i++) {
            boolean bit = ((value >> (length - 1 - i)) & 1) == 1;
            bitset.set(startBit + i, bit);
        }
    }

    private static BitSet toBitSet(long value, int bits, int rightShift) {
        BitSet bs = new BitSet(64);
        long masked = (value >> rightShift) & ((1L << bits) - 1);
        for (int i = 0; i < bits; i++) {
            bs.set(i, ((masked >> (bits - 1 - i)) & 1) == 1);
        }
        return bs;
    }

    // 时钟序列生成
    private synchronized BitSet generateClockSequence() {
        if (clockSequence == 0) {
            clockSequence = (short) new SecureRandom().nextInt(0x3FFF);
        }
        return toBitSet(clockSequence, 14, 0);
    }

    // MAC地址获取
    private BitSet getMacAddressBits() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface network = interfaces.nextElement();
                if (!network.isLoopback() && network.isUp()) {
                    byte[] mac = network.getHardwareAddress();
                    if (mac != null && mac.length == 6) {
                        // 清除多播位（第7位）
                        mac[0] &= (byte) 0xFE;
                        return BitSet.valueOf(mac);
                    }
                }
            }
        } catch (Exception ignored) {}
        
        // 生成随机地址并设置本地位（第7位）
        byte[] randomNode = new byte[6];
        new SecureRandom().nextBytes(randomNode);
        randomNode[0] |= (byte) 0x80; // 设置本地管理位
        return BitSet.valueOf(randomNode);
    }

    // 转换为字节数组
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        writeBits(buffer, timeLow, 32);     // 0-3字节
        writeBits(buffer, timeMid, 16);     // 4-5字节
        writeBits(buffer, timeHighVer, 16); // 6-7字节
        writeBits(buffer, varClockSeq, 16); // 8-9字节
        writeBits(buffer, node, 48);        // 10-15字节
        return buffer.array();
    }

    private void writeBits(ByteBuffer buffer, BitSet bits, int bitLength) {
        byte[] bytes = new byte[bitLength / 8];
        for (int i = 0; i < bitLength; i++) {
            if (bits.get(i)) {
                int bytePos = i / 8;
                int bitPos = 7 - (i % 8);
                bytes[bytePos] |= (byte) (1 << bitPos);
            }
        }
        buffer.put(bytes);
    }

    // 标准UUID格式
    @Override
    public String toString() {
        byte[] bytes = toBytes();
        return String.format("%08x-%04x-%04x-%04x-%012x",
                ByteBuffer.wrap(bytes, 0, 4).getInt(),
                ByteBuffer.wrap(bytes, 4, 2).getShort(),
                ByteBuffer.wrap(bytes, 6, 2).getShort(),
                ByteBuffer.wrap(bytes, 8, 2).getShort(),
                // 手动处理6字节的node字段
                ((long) (bytes[10] & 0xFF) << 40) |
                        ((long) (bytes[11] & 0xFF) << 32) |
                        ((long) (bytes[12] & 0xFF) << 24) |
                        ((long) (bytes[13] & 0xFF) << 16) |
                        ((long) (bytes[14] & 0xFF) << 8) |
                        (bytes[15] & 0xFF)
        );
    }

    public static void main(String[] args) {
        System.out.println(new UUIDv1());
    }
}