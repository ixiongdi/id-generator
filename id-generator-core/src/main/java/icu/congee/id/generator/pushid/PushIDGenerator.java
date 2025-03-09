package icu.congee.id.generator.pushid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

public class PushIDGenerator implements IdGenerator {
    // 模拟 base64 安全字符，按 ASCII 排序
    private static final String PUSH_CHARS = "-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz";
    // 上次推送的时间戳，用于防止在同一毫秒内推送两次时的本地冲突
    private static long lastPushTime = 0;
    // 存储上次生成的随机字符，用于处理时间戳冲突
    private static int[] lastRandChars = new int[12];

    public static String generatePushID() {
        long now = System.currentTimeMillis();
        boolean duplicateTime = (now == lastPushTime);
        lastPushTime = now;

        // 存储时间戳转换后的字符
        char[] timeStampChars = new char[8];
        for (int i = 7; i >= 0; i--) {
            timeStampChars[i] = PUSH_CHARS.charAt((int) (now % 64));
            now = now / 64;
        }
        if (now != 0) {
            throw new RuntimeException("We should have converted the entire timestamp.");
        }

        StringBuilder id = new StringBuilder(new String(timeStampChars));

        if (!duplicateTime) {
            for (int i = 0; i < 12; i++) {
                lastRandChars[i] = (int) (Math.random() * 64);
            }
        } else {
            // 如果时间戳与上次推送相同，使用相同的随机数，但加 1
            for (int i = 11; i >= 0 && lastRandChars[i] == 63; i--) {
                lastRandChars[i] = 0;
            }
            lastRandChars[lastRandChars.length - 1]++;
        }

        for (int i = 0; i < 12; i++) {
            id.append(PUSH_CHARS.charAt(lastRandChars[i]));
        }

        if (id.length() != 20) {
            throw new RuntimeException("Length should be 20.");
        }

        return id.toString();
    }

    public static void main(String[] args) {
        // 测试生成 Push ID
        System.out.println(generatePushID());
    }

    @Override
    public String generate() {
        return generatePushID();
    }

    @Override
    public IdType idType() {
        return IdType.pushID;
    }
}