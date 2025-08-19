package uno.xifan.id.generator.custom;

import lombok.Data;

import java.lang.management.ManagementFactory;
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
    private final long timestamp; // 时间戳（纳秒）
    private final long counter; // 递增计数器
    private final long node; // 节点标识（MAC地址）
    private final long secureRandom; // 安全随机整数

    public EntropyKey() {
        this.timestamp = System.nanoTime(); // 使用纳秒级时间戳提高精度
        this.counter = COUNTER.getAndIncrement();
        this.node = NODE;
        this.secureRandom = SECURE_RANDOM.nextLong();
    }

    /** 初始化节点标识：通过 MAC 地址生成一个 48-bit 的 long 值 */
    private static long initializeNodeIdentifier() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            if (network != null) {
                byte[] mac = network.getHardwareAddress();
                if (mac != null) {
                    // 取 MAC 地址后 6 字节（48位）作为前半部分
                    long macPart = 0;
                    for (int i = 0; i < Math.min(6, mac.length); i++) {
                        macPart = (macPart << 8) | (mac[i] & 0xFF); // 逐字节拼接MAC地址
                    }
                    // 获取当前进程PID并截取低16位作为后半部分
                    // Java 8兼容方案：通过RuntimeMXBean获取进程名称（格式：pid@hostname）
                    String processName = ManagementFactory.getRuntimeMXBean().getName();
                    long pid = Long.parseLong(processName.split("@")[0]);
                    long pidPart = pid & 0xFFFFL; // 保留PID的低16位（0-65535）
                    // 组合：前48位MAC地址 << 16位 + 后16位PID
                    return (macPart << 16) | pidPart;
                }
            }
        } catch (Exception e) {
            // 忽略异常，使用随机值替代
        }

        // 如果无法获取 MAC 地址或PID，则使用安全随机数生成64位标识
        return SECURE_RANDOM.nextLong();
    }
}
