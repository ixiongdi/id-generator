# KSUID (K-Sortable Globally Unique IDs)

## 概述
KSUID是一种全局唯一标识符，由Segment开发并开源。它结合了时间戳和随机数，提供了良好的时间排序性和全局唯一性保证。

## 特点
- **K-可排序性**：KSUID包含时间戳组件，支持按时间顺序排序
- **全局唯一性**：通过组合时间戳和随机数确保唯一性
- **高性能**：生成过程简单高效，适合高并发场景
- **时间可追溯**：可以从ID中提取生成时间

## 结构
KSUID由两部分组成：
1. **时间戳部分**：32位，精确到秒
2. **随机数部分**：128位，提供充分的随机性

## 使用方法

### 基本用法
```java
// 获取标准KSUID
Ksuid ksuid = KsuidCreator.getKsuid();

// 使用指定时间创建KSUID
Ksuid customKsuid = KsuidCreator.getKsuid(Instant.now());
```

### 高精度时间戳
```java
// 获取亚秒级精度的KSUID
Ksuid subsecondKsuid = KsuidCreator.getSubsecondKsuid();
```

### 单调递增
```java
// 获取保证单调递增的KSUID
Ksuid monotonicKsuid = KsuidCreator.getMonotonicKsuid();
```

## 应用场景
- 分布式系统中的唯一标识生成
- 需要按时间排序的数据ID
- 高并发环境下的ID生成
- 时序数据的主键生成

## 参考
- 原始实现：[Segment KSUID](https://github.com/segmentio/ksuid)
- 提交版本：commit bf376a7, July 2020

