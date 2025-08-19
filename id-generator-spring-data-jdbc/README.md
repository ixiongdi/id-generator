# ID Generator Spring Data JDBC

该模块提供了基于Spring Data JDBC的分布式ID生成器实现，适用于需要使用关系型数据库存储和管理ID生成状态的场景。

## 功能特点
- 集成Spring Data JDBC，支持多种关系型数据库
- 提供基于数据库自增序列的ID生成策略
- 支持分布式环境下的ID唯一性保证
- 提供灵活的配置选项

## 快速开始

### 依赖引入
```xml
<dependency>
    <groupId>uno.xifan</groupId>
    <artifactId>id-generator-spring-data-jdbc</artifactId>
    <version>${id-generator.version}</version>
</dependency>
```

### 配置示例
```properties
# 数据库ID生成器配置
id-generator.jdbc.table-name=id_generator
id-generator.jdbc.segment-size=1000
```

### 使用示例
```java
@Autowired
private JdbcIdGenerator jdbcIdGenerator;

public Long generateId() {
    return jdbcIdGenerator.nextId();
}
```

## 文档
- [完整文档](https://github.com/ixiongdi/id-generator/wiki)
- [API文档](https://ixiongdi.github.io/id-generator/apidocs/)

## 许可证
本项目基于 [MIT](LICENSE) 和 [Apache License 2.0](LICENSE) 双许可证开源。