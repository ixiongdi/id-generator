# TtsId 生成算法介绍

## 概述
TtsId（Timestamp-ThreadId-Sequence ID）是一种分布式ID生成算法，结合时间戳、线程ID和序列号来生成唯一ID。其设计目标是在高并发场景下生成有序、唯一且可解析的ID。

## ID结构
标准TtsId的结构如下：
- **41位时间戳**：精确到毫秒，可支持约69年的时间范围（从1970年开始）。
- **10位线程ID**：用于区分不同线程，支持最多1024个线程。
- **12位序列号**：每个线程每毫秒内可生成4096个唯一ID（范围0-4095），序列号溢出时重置为0。

总长度为64位（8字节），可直接转换为长整型（long）存储。

## 生成逻辑
TtsId的生成通过`TtsIdGenerator`类实现，核心逻辑如下：
1. **线程ID分配**：使用Redis的`RAtomicLong`生成全局唯一的线程ID，确保不同线程获取到唯一的ID（参考<mcfile name="TtsIdGenerator.java" path="c:\Users\76932\ktnb\id-generater\id-generator-spring-redis\src\main\java\icu\congee\id\generator\distributed\ttsid\TtsIdGenerator.java"></mcfile>构造方法）。
2. **线程本地存储**：通过`ThreadLocal`存储每个线程的当前序列号，避免多线程竞争。
3. **序列号管理**：每个线程每毫秒内递增序列号，当序列号达到最大值（4095）时重置为0，等待下一个时间戳周期。

示例代码（`TtsIdGenerator.java`）：
```java
@Override
public TtsId generate() {
    TtsIdThreadLocalHolder holder = threadLocalHolder.get();
    if (holder.sequence > MAX_SEQUENCE) {
        holder.sequence = 0;
    }
    return new TtsId(TtsId.currentTimestamp(), holder.threadId, holder.sequence++);
}
```

## 变体版本
为满足不同场景需求，TtsId还有以下变体：

### TtsIdPlus
- **44位时间戳**：精确到毫秒，扩展时间范围。
- **20位线程ID**：支持更多线程（参考<mcfile name="TtsIdPlus.java" path="c:\Users\76932\ktnb\id-generater\id-generator-spring-redis\src\main\java\icu\congee\id\generator\distributed\ttsid\TtsIdPlus.java"></mcfile>类定义）。
- **16位序列号**：每毫秒可生成65536个ID。
- **80位总长度**：无法用长整型表示，支持Base32编码（`toBase32`方法）。

### TtsIdPro
- **56位时间戳**：精确到微秒，时间精度更高（`currentTimestamp`方法返回微秒级时间）。
- **24位线程ID**：支持更多线程（参考<mcfile name="TtsIdPro.java" path="c:\Users\76932\ktnb\id-generater\id-generator-spring-redis\src\main\java\icu\congee\id\generator\distributed\ttsid\TtsIdPro.java"></mcfile>类定义）。
- **16位序列号**：每微秒可生成65536个ID。
- **12字节二进制表示**：支持Base16编码（`toBase16`方法）。

### TtsIdProMax
- **64位时间戳**：精确到纳秒，时间精度极高（`currentTimestamp`方法返回纳秒级时间）。
- **32位线程ID**：支持海量线程（参考<mcfile name="TtsIdProMax.java" path="c:\Users\76932\ktnb\id-generater\id-generator-spring-redis\src\main\java\icu\congee\id\generator\distributed\ttsid\TtsIdProMax.java"></mcfile>类定义）。
- **32位序列号**：极大扩展单周期内的ID生成量。
- **16字节二进制表示**：适用于对精度和扩展性要求极高的场景。

## 序列化方法
TtsId支持多种序列化格式：
- `toLong()`：转换为64位长整型（仅标准TtsId支持，参考<mcfile name="TtsId.java" path="c:\Users\76932\ktnb\id-generater\id-generator-spring-redis\src\main\java\icu\congee\id\generator\distributed\ttsid\TtsId.java"></mcfile>的`toLong`方法）。
- `toBytes()`：转换为二进制字节数组。
- `toBase16()`：转换为Base16（十六进制）字符串。
- `toBase32()`（TtsIdPlus）：针对80位设计的Base32编码，更紧凑（参考<mcfile name="TtsIdPlus.java" path="c:\Users\76932\ktnb\id-generater\id-generator-spring-redis\src\main\java\icu\congee\id\generator\distributed\ttsid\TtsIdPlus.java"></mcfile>的`toBase32`方法）。

## 依赖与配置
TtsId生成器依赖Redisson客户端来管理线程ID，需在Spring上下文中配置`RedissonClient`实例。构造函数示例（`TtsIdGenerator.java`）：
```java
public TtsIdGenerator(RedissonClient redisson) {
    RAtomicLong threadId = redisson.getAtomicLong("IdGenerator:TtsIdGenerator:threadId");
    threadLocalHolder = ThreadLocal.withInitial(() -> new TtsIdThreadLocalHolder((short) threadId.getAndIncrement(), (short) 0));
}
```

## 适用场景
- 高并发分布式系统的唯一ID生成。
- 需要ID按时间有序的场景（如日志排序、数据库索引）。
- 对ID解析性有要求的场景（可从ID中提取时间、线程信息）。