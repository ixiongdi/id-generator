package uno.xifan.id.generator.flakeid;

import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Flake ID 生成器在分布式环境中生成 k 有序、无冲突的 ID。
 */
public class FlakeId {
    // 2 的 10 次方
    private static final long POW10 = 1L << 10;
    // 2 的 26 次方
    private static final long POW26 = 1L << 26;

    // 生成器选项
    private final Options options;
    // 生成器标识符
    private final long id;
    // 生成器标识符左移 12 位后的值
    private final long genId;
    // 用于减少生成时间戳值的数字
    private final long epoch;
    // 序列掩码
    private final long seqMask;
    // 序列计数器
    private long seq;
    // 上一次生成 ID 的时间
    private long lastTime;
    // 序列是否溢出的标志
    private boolean overflow;

    /**
     * 表示一个 ID 生成器。
     * @param options 生成器选项
     */
    public FlakeId(Options options) {
        this.options = options != null ? options : new Options();

        // 从 'id' 选项或 'datacenter' 和 'worker' 的组合设置生成器 ID
        if (this.options.id != null) {
            this.id = this.options.id & 0x3FF;
        } else {
            long datacenter = (this.options.datacenter != null ? this.options.datacenter : 0) & 0x1F;
            long worker = (this.options.worker != null ? this.options.worker : 0) & 0x1F;
            this.id = (datacenter << 5) | worker;
        }
        this.genId = this.id << 12;  // 生成器标识符 - 在生成 ID 时不会改变
        this.epoch = this.options.epoch != null ? this.options.epoch : 0;
        this.seq = 0;
        this.lastTime = 0;
        this.overflow = false;
        this.seqMask = this.options.seqMask != null ? this.options.seqMask : 0xFFF;
    }

    /**
     * 生成无冲突的 ID
     * @return 生成的 ID
     * @throws Exception 如果时钟回拨或者序列超过其最大值
     */
    public byte[] next() throws Exception {
        byte[] id = new byte[8];
        long time = new Date().getTime() - epoch;

        // 生成的 ID 与上一个 ID 在同一毫秒内
        if (time < lastTime) {
            throw new Exception("Clock moved backwards. Refusing to generate id for " + (lastTime - time) + " milliseconds");
        }
        if (time == lastTime) {
            // 如果当前毫秒内所有序列值（包括 0 共 4096 个唯一值）都已用于生成 ID
            // （溢出标志为 true），则等待下一毫秒
            if (overflow) {
                waitForNextMillis();
                return next();
            }

            // 增加序列计数器
            this.seq = (this.seq + 1) & seqMask;

            // 序列计数器超过其最大值（4095）
            // - 设置溢出标志并等待下一毫秒
            if (this.seq == 0) {
                this.overflow = true;
                waitForNextMillis();
                return next();
            }
        } else {
            this.overflow = false;
            this.seq = 0;
        }
        this.lastTime = time;

        ByteBuffer buffer = ByteBuffer.wrap(id);
        buffer.putInt(4, (int) (((time & 0x3) << 22) | genId | seq));
        buffer.put(4, (byte) ((time >> 2) & 0xFF));
        buffer.putShort(2, (short) ((time >> 10) & 0xFFFF));
        buffer.putShort(0, (short) ((time >> 26) & 0xFFFF));

        return id;
    }

    /**
     * 等待下一毫秒
     */
    private void waitForNextMillis() throws InterruptedException {
        long currentTime = new Date().getTime();
        while (currentTime <= lastTime) {
            currentTime = new Date().getTime();
            Thread.sleep(1);
        }
        lastTime = currentTime;
        overflow = false;
        seq = 0;
    }

    /**
     * 生成器选项类
     */
    public static class Options {
        // 生成器标识符
        public Long id;
        // 数据中心标识符
        public Long datacenter;
        // 工作节点标识符
        public Long worker;
        // 用于减少生成时间戳值的数字
        public Long epoch;
        // 序列掩码
        public Long seqMask;
    }
}