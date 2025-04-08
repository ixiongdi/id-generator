# Globally Unique ID Generator

[![Maven Central](https://img.shields.io/maven-central/v/icu.congee/id-generator.svg)](https://search.maven.org/search?q=g:icu.congee%20AND%20a:id-generator) [![License](https://img.shields.io/badge/license-MIT-red.svg)](LICENSE)

Xid 是一个全局唯一 ID 生成器库，可以安全地直接在服务器代码中使用。

Xid 使用 MongoDB 的 ObjectID 算法生成全局唯一 ID，但使用不同的序列化方式（[base32hex](https://datatracker.ietf.org/doc/html/rfc4648#page-10)）使其在字符串传输时更短：
https://docs.mongodb.org/manual/reference/object-id/

ID 由以下部分组成：

- 4 字节值表示 Unix 纪元以来的秒数
- 3 字节机器标识符
- 2 字节进程 ID
- 3 字节计数器（从随机值开始）

ID 的二进制表示与 MongoDB 12 字节 ObjectID 兼容。字符串表示使用[base32hex](https://datatracker.ietf.org/doc/html/rfc4648#page-10)（无填充）以获得更好的空间效率（20 字节）。使用 base32 的 hex 变体是为了保持 ID 的可排序性。

Xid 不使用 base64，因为大小写敏感性和 2 个非字母数字字符在各系统间传输时可能会有问题。也没有选择 base36，因为：1）它不是标准的；2）结果大小不可预测（不是位对齐的）；3）不能保持可排序性。要验证 base32 `xid`，需要一个 20 个字符长的、全部小写的`a`到`v`字母和`0`到`9`数字序列（`[0-9a-v]{20}`）。

UUID 是 16 字节（128 位），字符串表示为 36 个字符。Twitter 的 Snowflake ID 是 8 字节（64 位），但需要机器/数据中心配置和/或中央生成器服务器。xid 介于两者之间，为 12 字节（96 位），具有更紧凑的 URL 安全字符串表示（20 个字符）。不需要配置或中央生成器服务器，因此可以直接在服务器代码中使用。

| 名称        | 二进制大小 | 字符串大小   | 特性                                     |
| ----------- | ---------- | ------------ | ---------------------------------------- |
| [UUID]      | 16 字节    | 36 字符      | 无需配置，不可排序                       |
| [shortuuid] | 16 字节    | 22 字符      | 无需配置，不可排序                       |
| [Snowflake] | 8 字节     | 最多 20 字符 | 需要机器/DC 配置，需要中央服务器，可排序 |
| [MongoID]   | 12 字节    | 24 字符      | 无需配置，可排序                         |
| xid         | 12 字节    | 20 字符      | 无需配置，可排序                         |

[UUID]: https://en.wikipedia.org/wiki/Universally_unique_identifier
[shortuuid]: https://github.com/stochastic-technologies/shortuuid
[Snowflake]: https://blog.twitter.com/2010/announcing-snowflake
[MongoID]: https://docs.mongodb.org/manual/reference/object-id/

特性：

- 大小：12 字节（96 位），比 UUID 小，比 snowflake 大
- 默认使用 base32 hex 编码（作为可打印字符串传输时为 20 个字符，仍可排序）
- 无需配置，无需设置唯一的机器和/或数据中心 ID
- K 排序
- 嵌入时间，精确到 1 秒
- 每台主机/进程每秒保证 16,777,216（24 位）个唯一 ID
- 无锁（不像 UUIDv1 和 v2）

注意：

- Xid 依赖于系统时间和单调计数器，因此不具有密码学安全性。如果 ID 的不可预测性很重要，则不应使用 Xid。值得注意的是，大多数其他类 UUID 实现也不具有密码学安全性。如果需要真正的随机 ID 生成器，应使用依赖于密码学安全源的库。

## 使用方法

```xml
<dependency>
    <groupId>icu.congee</groupId>
    <artifactId>id-generator</artifactId>
    <version>${latest.version}</version>
</dependency>
```

```java
Xid xid = Xid.next();
System.out.println(xid.toString());
// 输出: 9m4e2mr0ui3e8a215n4g

// 获取xid的嵌入信息
xid.machine();
xid.pid();
xid.time();
xid.counter();
```

## 许可证

所有源代码均采用[MIT 许可证](LICENSE)授权。
