package icu.congee;

public interface IDGenerater {
    // 生成单个ID，返回Object以支持不同类型
    Object generateId();

    // 批量生成ID，默认实现
    default Object[] generateIds(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive.");
        }
        Object[] ids = new Object[count];
        for (int i = 0; i < count; i++) {
            ids[i] = generateId();
        }
        return ids;
    }


    // 获取ID生成器的类型
    String getType();
}