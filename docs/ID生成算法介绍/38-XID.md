# XID 生成算法介绍

## 一、概述
XID 是一种高性能、有序、紧凑的唯一ID生成算法，主要用于分布式系统中生成全局唯一标识符。其设计目标是在保证唯一性的同时，提供更好的排序性能和存储效率，适用于需要快速索引或排序的场景。

## 二、ID结构
XID 的标准格式为12字节（96位）的二进制数据，可编码为20字符的URL安全Base32字符串。具体结构如下：

| 字段         | 长度（字节） | 描述                         |
|--------------|--------------|------------------------------|
| 时间戳       | 4            | 精确到秒的Unix时间戳          |
| 机器ID       | 3            | 机器/进程唯一标识符（通常为MAC地址或自定义） |
| 进程ID       | 2            | 进程/线程ID                   |
| 计数器       | 3            | 同一秒内的自增计数器（最大4百万+） |

## 三、生成原理
1. **时间戳**：使用Unix时间（秒级）保证基本有序性，避免时钟回拨问题（需系统时间同步）。
2. **机器ID**：通过读取网卡MAC地址或用户自定义值确保不同机器生成的ID不冲突。
3. **进程ID**：区分同一机器上的不同进程，防止多实例冲突。
4. **计数器**：同一秒内的请求通过自增计数器保证唯一性，超出最大值时阻塞至下一秒。

## 四、示例代码（Java）
```java
import xid.XID;

public class XIDExample {
    public static void main(String[] args) {
        // 生成XID实例
        XID xid = new XID();
        // 获取二进制ID（12字节）
        byte[] binaryId = xid.toBytes();
        // 获取Base32字符串（20字符）
        String stringId = xid.toString();
        System.out.println("Binary ID: " + bytesToHex(binaryId));
        System.out.println("String ID: " + stringId);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
```

## 五、特点
- **有序性**：基于时间戳生成，支持按ID排序（优于UUID）。
- **紧凑性**：12字节二进制或20字符字符串，存储效率高于UUID（16字节/36字符）。
- **高性能**：纯内存计算，生成速度可达百万级/秒。
- **防冲突**：多维度（时间、机器、进程、计数器）保证全局唯一。

## 六、适用场景
- 分布式数据库主键（如MySQL、MongoDB）。
- 日志系统唯一标识（需快速排序检索）。
- 分布式缓存键（减少存储开销）。
- 实时系统消息ID（需高吞吐量生成）。