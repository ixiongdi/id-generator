package icu.congee.id.generator.borid;

import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.function.Function;

/**
 * 基于本机特征值的BorId部分实现
 * 默认实现为获取本机48位Mac地址+16位Pid
 */
public class EigenvalueBorIdPart implements BorIdPart {

    private final int bits;
    private final Function<Byte, BitSet> function;

    /**
     * 构造函数
     * 
     * @param bits 位长度，必须是8的倍数
     */
    public EigenvalueBorIdPart(int bits) {
        this(bits, null);
    }

    /**
     * 构造函数
     * 
     * @param bits     位长度
     * @param function 自定义值生成器
     */
    public EigenvalueBorIdPart(int bits, Function<Byte, BitSet> function) {
        this.bits = bits;
        this.function = function;
    }


    @Override
    public int getBits() {
        return bits;
    }

    @Override
    public BitSet next() {
        return this.function.apply((byte) this.bits);
    }

    /**
     * 生成基于本机特征值的BitSet
     * 
     * @return BitSet对象
     */
    private BitSet generateValue() {
        BitSet bitSet = new BitSet(bits);

        // 获取MAC地址（48位）
        byte[] mac = getMacAddress();

        // 获取进程ID（16位）
        int pid = getProcessId();

        // 将MAC地址转换为BitSet（最多48位）
        int bitIndex = 0;
        for (int i = 0; i < Math.min(mac.length * 8, bits); i++) {
            if ((mac[i / 8] & (1 << (i % 8))) != 0) {
                bitSet.set(i);
            }
            bitIndex = i + 1;
        }

        // 将PID转换为BitSet（如果还有空间，最多16位）
        for (int i = 0; i < Math.min(16, bits - bitIndex); i++) {
            if ((pid & (1 << i)) != 0) {
                bitSet.set(bitIndex + i);
            }
        }

        return bitSet;
    }

    /**
     * 获取本机MAC地址
     * 
     * @return MAC地址字节数组
     */
    private byte[] getMacAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (!networkInterface.isLoopback() && !networkInterface.isVirtual() && networkInterface.isUp()) {
                    byte[] mac = networkInterface.getHardwareAddress();
                    if (mac != null && mac.length == 6) {
                        return mac;
                    }
                }
            }
        } catch (SocketException e) {
            // 如果无法获取MAC地址，返回一个默认值
        }

        // 如果无法获取MAC地址，返回一个默认值
        return new byte[] { 0, 0, 0, 0, 0, 0 };
    }

    /**
     * 获取当前进程ID
     * 
     * @return 进程ID
     */
    private int getProcessId() {
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        try {
            return Integer.parseInt(processName.split("@")[0]);
        } catch (Exception e) {
            // 如果无法获取进程ID，返回一个默认值
            return 0;
        }
    }
}