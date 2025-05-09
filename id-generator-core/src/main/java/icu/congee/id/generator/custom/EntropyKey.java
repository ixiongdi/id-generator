package icu.congee.id.generator.custom;

import lombok.Data;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class EntropyKey {

    private static final AtomicLong COUNTER = new AtomicLong(0);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    // 获取本机 MAC 地址作为节点标识
    private static final long NODE = initializeNodeIdentifier();
    private final long timestamp;         // 时间戳（纳秒）
    private final long counter;           // 递增计数器
    private final long node;              // 节点标识（MAC地址）
    private final long secureRandom;      // 安全随机整数

    public EntropyKey() {
        this.timestamp = System.nanoTime(); // 使用纳秒级时间戳提高精度
        this.counter = COUNTER.getAndIncrement();
        this.node = NODE;
        this.secureRandom = SECURE_RANDOM.nextLong();
    }

    /**
     * 初始化节点标识：通过 MAC 地址生成一个 48-bit 的 long 值
     */
    private static long initializeNodeIdentifier() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            if (network != null) {
                byte[] mac = network.getHardwareAddress();
                if (mac != null) {
                    // 取 MAC 地址后 6 字节组成一个 long（48 位）
                    long node = 0;
                    for (int i = 0; i < Math.min(6, mac.length); i++) {
                        node = (node << 8) | (mac[i] & 0xFF);
                    }
                    return node;
                }
            }
        } catch (Exception e) {
            // 忽略异常，使用随机值替代
        }

        // 如果无法获取 MAC 地址，则使用安全随机数模拟唯一节点标识
        return SECURE_RANDOM.nextLong() & 0x0000FFFFFFFFFFFFL; // 限制为 48 位
    }
}