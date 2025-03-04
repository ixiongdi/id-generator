package com.github.ixiongdi.id.generator;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class WorkerIdGenerator {
    public static long getWorkerId() {
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            return hash(hostName);
        } catch (UnknownHostException e) {
            throw new RuntimeException("无法获取主机名", e);
        }
    }

    private static long hash(String str) {
        long h = 1125899906842597L; // 一个大质数作为种子
        for (char c : str.toCharArray()) {
            h = 31 * h + c;
        }
        return h;
    }
}