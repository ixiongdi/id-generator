package icu.congee.id.generator.util.time;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * NTP协议客户端实现
 *
 * <p>支持RFC 5905规范，提供毫秒级时间同步</p>
 */
public class NtpClient {
    private static final int NTP_PORT = 123;
    private static final int NTP_PACKET_SIZE = 48;
    private static final long NTP_TIMESTAMP_OFFSET = 2208988800L;

    /**
     * 从NTP服务器获取当前时间
     * @param server 服务器地址
     * @return 校准后的Instant时间对象
     * @throws Exception 网络或协议异常
     */
    public static Instant getTime(String server) throws Exception {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(5000);
            
            byte[] buffer = new byte[NTP_PACKET_SIZE];
            buffer[0] = 0x1B; // LI=0, Version=3, Mode=3 (client)

            InetAddress address = InetAddress.getByName(server);
            DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, NTP_PORT);
            socket.send(request);

            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);

            long seconds = read32(buffer, 40);
            long fraction = read32(buffer, 44);
            
            return Instant.ofEpochSecond(seconds - NTP_TIMESTAMP_OFFSET)
                    .plusNanos((fraction * 1000_000_000L) >>> 32);
        }
    }

    /**
     * 获取带时区偏移的时间（适用于需要本地时间转换的场景）
     */
    public static OffsetDateTime getTimeWithOffset(String server, ZoneOffset offset) throws Exception {
        return getTime(server).atOffset(offset);
    }

    private static long read32(byte[] buffer, int offset) {
        return ((long) (buffer[offset] & 0xFF)) << 24
                | ((buffer[offset + 1] & 0xFF) << 16)
                | ((buffer[offset + 2] & 0xFF) << 8)
                | (buffer[offset + 3] & 0xFF);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("NTP时间同步测试：");
        Instant time = getTime("pool.ntp.org");
        System.out.println("UTC时间：" + time);
        System.out.println("本地时间：" + time.atOffset(ZoneOffset.systemDefault().getRules().getOffset(time)));
    }
}