package icu.congee;

public class AutoIncrementIDGenerater implements IDGenerater {
    private long currentId; // 当前ID
    private int step;       // 步长
    private final long startId; // 初始ID，用于重置

    public AutoIncrementIDGenerater(long startId, int step) {
        if (step <= 0) {
            throw new IllegalArgumentException("Step must be positive.");
        }
        this.startId = startId;
        this.currentId = startId - step; // 确保第一次generateId返回startId
        this.step = step;
    }

    @Override
    public Object generateId() {
        currentId += step;
        return currentId;
    }


    @Override
    public String getType() {
        return "AutoIncrement";
    }
}