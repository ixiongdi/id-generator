package icu.congee.id.generator.web.service;

/**
 * ID服务接口，定义了ID的存储操作。
 * 该接口提供了将生成的ID存储到指定表中的功能。
 */
public interface IdService {

    /**
     * 将生成的ID插入到指定的表中。
     *
     * @param table 目标表名
     * @param id    要插入的ID值
     */
    void insert(String table, Object id);
}
