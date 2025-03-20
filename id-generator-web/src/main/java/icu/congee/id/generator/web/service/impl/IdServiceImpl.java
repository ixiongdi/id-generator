package icu.congee.id.generator.web.service.impl;

import com.mybatisflex.core.row.DbChain;

import icu.congee.id.generator.web.service.IdService;

import org.springframework.stereotype.Service;

/**
 * ID服务接口的实现类，负责将生成的ID存储到数据库中。
 * 该类使用MyBatis-Flex框架实现数据库操作。
 */
@Service
public class IdServiceImpl implements IdService {

    /**
     * 默认构造器创建ID服务实例
     * <p>
     * 创建一个新的ID服务实现实例，用于处理ID的存储操作。
     * 该实例会被Spring容器管理，并提供ID存储服务。
     * </p>
     */

    /**
     * 将生成的ID插入到指定的表中。
     * 使用DbChain构建器实现动态表名的数据插入。
     *
     * @param table 目标表名
     * @param id    要插入的ID值
     */
    @Override
    public void insert(String table, Object id) {
        DbChain.table(table).set("id", id).save();
    }
}
