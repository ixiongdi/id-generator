package icu.congee.id.generator.sonyflake;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

// Package sonyflake 实现了Sonyflake，一个受Twitter的Snowflake启发的分布式唯一ID生成器。
//
// 一个Sonyflake ID由以下部分组成：
//
// 39位用于时间，单位为10毫秒
//  8位用于序列号
// 16位用于机器ID
public class Sonyflake {
    // 这些常量是Sonyflake ID各部分的位长度。
    private static final int BIT_LEN_TIME = 39; // 时间的位长度
    private static final int BIT_LEN_SEQUENCE = 8; // 序列号的位长度
    private static final int BIT_LEN_MACHINE_ID = 63 - BIT_LEN_TIME - BIT_LEN_SEQUENCE; // 机器ID的位长度

    private static final long SONYFLAKE_TIME_UNIT = 10_000_000; // 纳秒，即10毫秒

    private static final long DEFAULT_START_TIME = toSonyflakeTime(
            new GregorianCalendar(2014, Calendar.SEPTEMBER, 1, 0, 0, 0).getTime());

    private final Object mutex = new Object();
    private long startTime;
    private long elapsedTime;
    private short sequence;
    private short machineID;

    // 异常类
    public static class StartTimeAheadException extends Exception {
        public StartTimeAheadException() {
            super("start time is ahead of now");
        }
    }

    public static class NoPrivateAddressException extends Exception {
        public NoPrivateAddressException() {
            super("no private ip address");
        }
    }

    public static class OverTimeLimitException extends Exception {
        public OverTimeLimitException() {
            super("over the time limit");
        }
    }

    public static class InvalidMachineIDException extends Exception {
        public InvalidMachineIDException() {
            super("invalid machine id");
        }
    }

    // Settings 配置Sonyflake：
    //
    // StartTime 是Sonyflake时间被定义为经过时间的起始时间。
    // 如果StartTime为0，Sonyflake的起始时间被设置为"2014-09-01 00:00:00 +0000 UTC"。
    // 如果StartTime超前于当前时间，Sonyflake不会被创建。
    //
    // MachineID 返回Sonyflake实例的唯一ID。
    // 如果MachineID返回错误，Sonyflake不会被创建。
    // 如果MachineID为null，将使用默认的MachineID。
    // 默认的MachineID返回私有IP地址的低16位。
    //
    // CheckMachineID 验证机器ID的唯一性。
    // 如果CheckMachineID返回false，Sonyflake不会被创建。
    // 如果CheckMachineID为null，不进行验证。
    public static class Settings {
        public Date startTime;
        public MachineIDSupplier machineIDSupplier;
        public MachineIDValidator machineIDValidator;

        public Settings() {
            this.startTime = null;
            this.machineIDSupplier = null;
            this.machineIDValidator = null;
        }
    }

    @FunctionalInterface
    public interface MachineIDSupplier {
        short get() throws Exception;
    }

    @FunctionalInterface
    public interface MachineIDValidator {
        boolean validate(short machineID);
    }

    // New 返回一个使用给定Settings配置的新Sonyflake。
    // 在以下情况下，New会返回错误：
    // - Settings.StartTime超前于当前时间。
    // - Settings.MachineID返回错误。
    // - Settings.CheckMachineID返回false。
    public static Sonyflake newInstance(Settings st) throws StartTimeAheadException, NoPrivateAddressException, InvalidMachineIDException {
        if (st.startTime != null && st.startTime.after(new Date())) {
            throw new StartTimeAheadException();
        }

        Sonyflake sf = new Sonyflake();
        sf.sequence = (short) ((1 << BIT_LEN_SEQUENCE) - 1);

        if (st.startTime == null) {
            sf.startTime = DEFAULT_START_TIME;
        } else {
            sf.startTime = toSonyflakeTime(st.startTime);
        }

        short machineID;
        try {
            if (st.machineIDSupplier == null) {
                machineID = lower16BitPrivateIP();
            } else {
                machineID = st.machineIDSupplier.get();
            }
        } catch (Exception e) {
            throw new NoPrivateAddressException();
        }

        if (st.machineIDValidator != null && !st.machineIDValidator.validate(machineID)) {
            throw new InvalidMachineIDException();
        }

        sf.machineID = machineID;
        return sf;
    }

    // NewSonyflake 返回一个使用给定Settings配置的新Sonyflake。
    // 在以下情况下，NewSonyflake会返回null：
    // - Settings.StartTime超前于当前时间。
    // - Settings.MachineID返回错误。
    // - Settings.CheckMachineID返回false。
    public static Sonyflake newSonyflake(Settings st) {
        try {
            return newInstance(st);
        } catch (Exception e) {
            return null;
        }
    }

    // NextID 生成下一个唯一ID。
    // 当Sonyflake时间溢出后，NextID会返回错误。
    public long nextID() throws OverTimeLimitException {
        final short maskSequence = (short) ((1 << BIT_LEN_SEQUENCE) - 1);

        synchronized (mutex) {
            long current = currentElapsedTime(startTime);
            if (elapsedTime < current) {
                elapsedTime = current;
                sequence = 0;
            } else { // elapsedTime >= current，当前时间没有前进
                sequence = (short) ((sequence + 1) & maskSequence);
                if (sequence == 0) {
                    elapsedTime++;
                    long overtime = elapsedTime - current;
                    try {
                        Thread.sleep(sleepTime(overtime));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            return toID();
        }
    }

    private static long toSonyflakeTime(Date t) {
        return t.toInstant().toEpochMilli() * 1_000_000 / SONYFLAKE_TIME_UNIT;
    }

    private static long currentElapsedTime(long startTime) {
        return toSonyflakeTime(new Date()) - startTime;
    }

    private static long sleepTime(long overtime) {
        return overtime * SONYFLAKE_TIME_UNIT - (System.nanoTime() % SONYFLAKE_TIME_UNIT);
    }

    private long toID() throws OverTimeLimitException {
        if (elapsedTime >= (1L << BIT_LEN_TIME)) {
            throw new OverTimeLimitException();
        }

        return (elapsedTime << (BIT_LEN_SEQUENCE + BIT_LEN_MACHINE_ID)) |
                ((long) sequence << BIT_LEN_MACHINE_ID) |
                machineID;
    }

    private static byte[] privateIPv4() throws NoPrivateAddressException {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && address.getAddress().length == 4) {
                        byte[] ip = address.getAddress();
                        if (isPrivateIPv4(ip)) {
                            return ip;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            throw new NoPrivateAddressException();
        }
        throw new NoPrivateAddressException();
    }

    private static boolean isPrivateIPv4(byte[] ip) {
        // 允许私有IP地址（RFC1918）和链路本地地址（RFC3927）
        return ip != null &&
                (ip[0] == 10 || (ip[0] == (byte) 172 && (ip[1] >= 16 && ip[1] < 32)) ||
                        (ip[0] == (byte) 192 && ip[1] == (byte) 168) || (ip[0] == (byte) 169 && ip[1] == (byte) 254));
    }

    private static short lower16BitPrivateIP() throws NoPrivateAddressException {
        byte[] ip = privateIPv4();
        return (short) ((ip[2] & 0xFF) << 8 | (ip[3] & 0xFF));
    }

    // ElapsedTime 返回给定Sonyflake ID生成时的经过时间。
    public static long elapsedTime(long id) {
        return id >> (BIT_LEN_SEQUENCE + BIT_LEN_MACHINE_ID);
    }

    public static long sequenceNumber(long id) {
        final long maskSequence = ((1L << BIT_LEN_SEQUENCE) - 1) << BIT_LEN_MACHINE_ID;
        return (id & maskSequence) >> BIT_LEN_MACHINE_ID;
    }

    public static long machineID(long id) {
        final long maskMachineID = (1L << BIT_LEN_MACHINE_ID) - 1;
        return id & maskMachineID;
    }

    // Decompose 返回Sonyflake ID的各个部分组成的集合。
    public static Map<String, Long> decompose(long id) {
        long msb = id >> 63;
        long time = elapsedTime(id);
        long sequence = sequenceNumber(id);
        long machineID = machineID(id);
        Map<String, Long> result = new HashMap<>();
        result.put("id", id);
        result.put("msb", msb);
        result.put("time", time);
        result.put("sequence", sequence);
        result.put("machine-id", machineID);
        return result;
    }

    private Sonyflake() {
        this.startTime = 0;
        this.elapsedTime = 0;
        this.sequence = 0;
        this.machineID = 0;
    }
}