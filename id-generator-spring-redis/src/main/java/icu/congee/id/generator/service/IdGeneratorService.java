package icu.congee.id.generator.service;

/**
 * ID生成器服务接口
 */
public interface IdGeneratorService {
    /**
     * 生成下一个ID
     *
     * @param key 业务键名
     * @return 生成的ID
     */
    long nextId(String key);

    /**
     * 批量生成ID
     *
     * @param key 业务键名
     * @param size 批量大小
     * @return 生成的ID数组
     */
    long[] nextId(String key, int size);
}