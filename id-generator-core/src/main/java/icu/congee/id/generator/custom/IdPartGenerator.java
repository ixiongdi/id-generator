package icu.congee.id.generator.custom;

/**
 * ID部分生成器接口
 * <p>
 * 这是一个函数式接口，用于定义ID各部分的生成方法。
 * 用户可以通过实现此接口来自定义ID的各个部分（时间戳、工作节点ID、序列号、随机数）的生成逻辑。
 *
 * @param <T> 生成的ID部分的类型，可以是Long或String
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
@FunctionalInterface
public interface IdPartGenerator<T> {
    
    /**
     * 生成ID的一个部分
     *
     * @return 生成的ID部分
     */
    T generate();
    
    /**
     * 批量生成ID部分
     *
     * @param count 需要生成的数量
     * @return 生成的ID部分数组
     */
    default T[] generateBatch(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("生成数量必须大于0");
        }
        
        @SuppressWarnings("unchecked")
        T[] results = (T[]) new Object[count];
        for (int i = 0; i < count; i++) {
            results[i] = generate();
        }
        return results;
    }
}