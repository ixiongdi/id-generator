package uno.xifan.id.generator.pushid;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

/**
 * Firebase Push ID生成器的Java实现
 *
 * <p>
 * Push ID是一个20字符长的字符串，由以下部分组成：
 * - 前8个字符：基于时间戳（毫秒级）的编码
 * - 后12个字符：随机生成的字符
 *
 * <p>
 * 特点：
 * 1. 按时间排序：由于使用时间戳作为前缀，Push ID天然具有时间顺序
 * 2. 唯一性：使用随机字符和时间戳组合确保唯一性
 * 3.
 * URL安全：使用URL安全的字符集（-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz）
 *
 * @author congee
 */
public class PushIDGenerator implements IdGenerator {
    // 模拟 base64 安全字符，按 ASCII 排序
    private static final String PUSH_CHARS = "-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz";
    // 上次推送的时间戳，用于防止在同一毫秒内推送两次时的本地冲突
    private static long lastPushTime = 0;
    // 存储上次生成的随机字符，用于处理时间戳冲突
    private static int[] lastRandChars = new int[12];

    /**
     * 生成一个新的Push ID
     *
     * @return 20字符长的唯一Push ID
     * @throws RuntimeException 如果时间戳转换失败或生成的ID长度不正确
     */
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

    /**
     * 测试生成Push ID的主方法
     *
     * @param args 命令行参数（未使用）
     */
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
        return IdType.PushID;
    }
}