# CombGuid

## 简介
CombGuid是一种基于RT.Comb实现的可排序UUID变体，它通过将时间戳信息编码到UUID中，解决了标准UUID不可排序的问题。CombGuid保持了UUID的唯一性和随机性，同时提供了基于时间的排序能力。

## 实现原理
CombGuid通过重新排列标准UUID的字节结构来实现时间排序：
- 前6字节：Unix时间戳（精确到毫秒）
- 后10字节：随机UUID数据

这种结构设计确保了：
1. 保持UUID的128位长度不变
2. 在保留唯一性的同时支持时间排序
3. 向后兼容标准UUID格式

## 特点
### 优点
1. 可排序性：基于时间戳的前缀使得CombGuid天然支持时间排序
2. 高性能：生成过程简单，不需要额外的排序操作
3. 分布式友好：无需中心化协调，适合分布式系统
4. 兼容性：与标准UUID格式兼容，可以使用现有的UUID相关工具和库
5. 时间可追溯：可以从ID中提取生成时间

### 缺点
1. 时间戳精度限制：仅支持毫秒级精度
2. 非标准实现：作为UUID的变体实现，可能在某些场景下需要特殊处理

## 使用场景
1. 需要按时间排序的数据记录
2. 分布式系统中的有序ID生成
3. 数据库主键（特别是需要时间排序的场景）
4. 日志系统的消息ID

## 参考资料
- [RT.Comb原始实现](https://github.com/richardtallent/RT.Comb)
- [CombGuid规范说明](https://github.com/richardtallent/RT.Comb/blob/master/README.md)


