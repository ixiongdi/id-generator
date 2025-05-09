package icu.congee.id.generator.custom;

import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EntropySourceContainer {

    // 时间戳
    // 计数器
    // 节点
    // 随机数

    // === 各类熵源字段 ===
    private final long systemBootTime;            // 系统启动时间（秒级）
    private final String macAddress;              // MAC地址
    private final String ipAddress;               // IP地址
    private final String processId;               // 进程ID
    private final String hostName;                // 主机名
    private final int threadId;                   // 当前线程ID
    private final int secureRandomInt;            // 安全随机整数
    private final int counter;                    // 递增计数器
    private final String osName;                  // 操作系统名称
    private final String javaVersion;             // Java版本
    private final String userName;                // 用户名
    private final String userDir;                 // 用户目录
    private final String envVarsHash;             // 环境变量拼接字符串
    private final String filesystemRoots;         // 文件系统根路径
    private final String jvmArgs;                 // JVM启动参数
    private final int cpuCores;                   // CPU核心数
    private final long jvmStartTime;              // JVM启动时间
    private final long totalPhysicalMemory;       // 总物理内存
    private final String jvmVendor;               // JVM供应商
    private final String osArch;                  // 操作系统架构
    private final String osVersion;               // 操作系统版本

    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // 构造函数收集所有熵源
    public EntropySourceContainer() {
        this.systemBootTime = System.currentTimeMillis() / 1000L;
        this.macAddress = getMacAddress();
        this.ipAddress = getIpAddress();
        this.processId = getProcessId();
        this.hostName = getHostName();
        this.threadId = (int) Thread.currentThread().getId();
        this.secureRandomInt = SECURE_RANDOM.nextInt();
        this.counter = COUNTER.getAndIncrement();
        this.osName = System.getProperty("os.name", "unknown");
        this.javaVersion = System.getProperty("java.version", "unknown");
        this.userName = System.getProperty("user.name", "unknown");
        this.userDir = System.getProperty("user.dir", "unknown");
        this.envVarsHash = getEnvironmentVariablesHash();
        this.filesystemRoots = getFilesystemRoots();
        this.jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments().toString();
        this.cpuCores = Runtime.getRuntime().availableProcessors();
        this.jvmStartTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        this.totalPhysicalMemory = getTotalPhysicalMemory();
        this.jvmVendor = System.getProperty("java.vm.vendor", "unknown");
        this.osArch = System.getProperty("os.arch", "unknown");
        this.osVersion = System.getProperty("os.version", "unknown");
    }

    // === 熵源采集方法 ===

    private static String getMacAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (!iface.isLoopback() && iface.isUp()) {
                    byte[] mac = iface.getHardwareAddress();
                    if (mac != null) {
                        StringBuilder sb = new StringBuilder();
                        for (byte b : mac) {
                            sb.append(String.format("%02X:", b));
                        }
                        return sb.deleteCharAt(sb.length() - 1).toString();
                    }
                }
            }
        } catch (Exception ignored) {}
        return "00:00:00:00:00:00";
    }

    private static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (!iface.isLoopback() && iface.isUp()) {
                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                            return addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ignored) {
            return "0.0.0.0";
        }
    }

    private static String getProcessId() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (name.contains("@")) {
            return name.split("@")[0];
        }
        return "unknown";
    }

    private static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private static String getEnvironmentVariablesHash() {
        Map<String, String> env = System.getenv();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : env.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
        }
        return sb.toString();
    }

    private static String getFilesystemRoots() {
        StringBuilder sb = new StringBuilder();
        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            sb.append(root).append(":");
        }
        return sb.toString();
    }

    private static long getTotalPhysicalMemory() {
        com.sun.management.OperatingSystemMXBean bean =
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return bean.getTotalMemorySize();
    }

    // === 重写 equals 和 hashCode ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntropySourceContainer that)) return false;

        return systemBootTime == that.systemBootTime &&
               threadId == that.threadId &&
               counter == that.counter &&
               cpuCores == that.cpuCores &&
               Objects.equals(macAddress, that.macAddress) &&
               Objects.equals(ipAddress, that.ipAddress) &&
               Objects.equals(processId, that.processId) &&
               Objects.equals(hostName, that.hostName) &&
               secureRandomInt == that.secureRandomInt &&
               Objects.equals(osName, that.osName) &&
               Objects.equals(javaVersion, that.javaVersion) &&
               Objects.equals(userName, that.userName) &&
               Objects.equals(userDir, that.userDir) &&
               Objects.equals(envVarsHash, that.envVarsHash) &&
               Objects.equals(filesystemRoots, that.filesystemRoots) &&
               Objects.equals(jvmArgs, that.jvmArgs) &&
               jvmStartTime == that.jvmStartTime &&
               totalPhysicalMemory == that.totalPhysicalMemory &&
               Objects.equals(jvmVendor, that.jvmVendor) &&
               Objects.equals(osArch, that.osArch) &&
               Objects.equals(osVersion, that.osVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                systemBootTime,
                macAddress,
                ipAddress,
                processId,
                hostName,
                threadId,
                secureRandomInt,
                counter,
                osName,
                javaVersion,
                userName,
                userDir,
                envVarsHash,
                filesystemRoots,
                jvmArgs,
                cpuCores,
                jvmStartTime,
                totalPhysicalMemory,
                jvmVendor,
                osArch,
                osVersion
        );
    }

    // === 可选：用于调试输出 ===
    @Override
    public String toString() {
        return "EntropySourceContainer{" +
                "systemBootTime=" + systemBootTime +
                ", macAddress='" + macAddress + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", processId='" + processId + '\'' +
                ", hostName='" + hostName + '\'' +
                ", threadId=" + threadId +
                ", secureRandomInt=" + secureRandomInt +
                ", counter=" + counter +
                ", osName='" + osName + '\'' +
                ", javaVersion='" + javaVersion + '\'' +
                ", userName='" + userName + '\'' +
                ", userDir='" + userDir + '\'' +
                ", envVarsHash='" + envVarsHash + '\'' +
                ", filesystemRoots='" + filesystemRoots + '\'' +
                ", jvmArgs='" + jvmArgs + '\'' +
                ", cpuCores=" + cpuCores +
                ", jvmStartTime=" + jvmStartTime +
                ", totalPhysicalMemory=" + totalPhysicalMemory +
                ", jvmVendor='" + jvmVendor + '\'' +
                ", osArch='" + osArch + '\'' +
                ", osVersion='" + osVersion + '\'' +
                '}';
    }

    // === 测试主程序 ===
    public static void main(String[] args) {
        Set<EntropySourceContainer> containers = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            EntropySourceContainer container = new EntropySourceContainer();
            containers.add(container);
            System.out.println("HashCode #" + (i+1) + ": " + Integer.toHexString(container.hashCode()));
        }

        System.out.println("\nTotal unique hash codes: " + containers.size());
    }
}