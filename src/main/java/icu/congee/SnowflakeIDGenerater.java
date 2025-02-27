package icu.congee;

public class SnowflakeIDGenerater implements IDGenerater {
    private long machineId; // 机器ID
    private long sequence;  // 序列号

    public SnowflakeIDGenerater(int machineId) {
        if (machineId < 0 || machineId > 1023) { // 假设10位机器ID
            throw new IllegalArgumentException("Machine ID must be between 0 and 1023.");
        }
        this.machineId = machineId;
        this.sequence = 0;
    }

    @Override
    public Long generateId() {
        // 简化的雪花ID生成逻辑：时间戳 + 机器ID + 序列号
        long timestamp = System.currentTimeMillis();
        sequence = (sequence + 1) & 4095; // 假设12位序列号
        return (timestamp << 22) | (machineId << 12) | sequence;
    }



    @Override
    public String getType() {
        return "Snowflake";
    }

    // 特定于雪花ID的配置方法
    public void setMachineId(int machineId) {
        if (machineId < 0 || machineId > 1023) {
            throw new IllegalArgumentException("Machine ID must be between 0 and 1023.");
        }
        this.machineId = machineId;
    }
}