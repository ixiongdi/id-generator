# Java ID Generator

## 简介

本项目是基于互联网上比较流行的ID生成方案，结合个人实战，总结出的一套ID生成方案最佳实践。
2024年5月关于UUID的最新标准[RFC 9652](https://www.rfc-editor.org/rfc/rfc9562.html)发布了，该标准发布时参考了16种ID生成方案。
我阅读了其中每一种方案，发现这些方案基本有两种路线：
1. 基于Snowflake及其变体的的64bit整数方案
2. 基于UUID极其魔改的128bit字符串方案

接下来我将分析其方案的优劣，及提出我自己的方案。

### Snowflake及其变体

应该说目前Snowflake是非常流行的，但是Snowflake方案始终绕不过去的一道坎就是如何分配dataCenterId和workerId。如果手动分配非常繁琐，增加了运维的工作量。
如果是自动分配，则需要引入中间件。
所以我认为Snowflake方案其实更加适合单体架构和小规模系统的ID生成。
下面就是我认为的Snowflake的两种最佳实践

#### 1. 基于时间排序的强业务关联的ID
其组成由精确到秒的时间字符串`yyMMddHHmmss`加上0-9999的序列号组成，类似下面这些
```markdown
2503041526330000
2503041526330001
```
优点：
1. 64bit整数，对数据库和编程语言都有良好的支持
2. 不占用空间，可排序
3. 可阅读，时间可读
4. 强业务，序列号有很强的业务关联性
5. JavaScript友好，不要转字符串
缺点：
1. 只支持单机
2. 序列号有限，每秒并发只有10000

#### 2. 基于时间排序的强随机ID
该算法启发自UUID v7，给不希望使用字符串作为ID，并发量不那么大的情况下，可支持分布式
其是一个64bit的整型，其中高位的32bit是精确到秒的时间戳，低位的32bit是随机数
```markdown
2503041526330000
2503041526330001
```

优点：
1. 64bit整数，对数据库和编程语言都有良好的支持
2. 不占用空间，可排序
3. 随机性强，不可猜测
4. 在并发不大的情况下可分布式
缺点：
1. 不设定起始时间的话，只能用到2038年
2. 碰撞几率高于UUID v7

### UUID v7和自定义的UUID v8

其实只要认真阅读了RFC9562标准及其参考文献，就能知道UUID的前序标准RFC4122已经不满足当前计算机应用的需求了，于是才有了新的标准
UUID v6、UUID v7、UUIDv8。
新增的标准是基于前序标准的扩展，也是基于现在互联网上流行方案的整合。
其中UUID v6更多的是对以前UUID的兼容处理，有暴露隐私的风险。所以这里主要介绍UUID v7、UUIDv8。

新的UUID都会把可排序防止最重要的位置，这也是几乎所以的ID生成方法都需要遵循的，这是由现代数据库的特性决定的。

#### 基于unix时间排序的UUID v7

其由48bit的毫秒级时间戳和74bit的随机数组成，类似下面这样的
```markdown
-------------------------------------------
field       bits value
-------------------------------------------
unix_ts_ms  48   0x017F22E279B0
ver          4   0x7
rand_a      12   0xCC3
var          2   0b10
rand_b      62   0b01, 0x8C4DC0C0C07398F
-------------------------------------------
total       128
-------------------------------------------
final: 017F22E2-79B0-7CC3-98C4-DC0C0C07398F
```

优点：
1. 128bit长，对编程语言来说是长度36字节的字符串，对数据库来说是UUID类型（postgresql）或者128bit binary（mysql）
2. 可排序
3. 随机性强，不可猜测
4. 随机性强，碰撞性极低
5. 可分布式，无依赖

缺点：
1. 长度略长
2. 个别数据库支持度不佳
3. 
#### 完全自定义UUID v8

UUID v8的出现就是因为市面上的ID生成方案百花齐放，但又缺乏统一的标准。她的出现可以把之前的大部分ID方案纳入进来且以标准的UUID出现。

```markdown
-------------------------------------------
field     bits value
-------------------------------------------
custom_a  48   0x2489E9AD2EE2
ver        4   0x8
custom_b  12   0xE00
var        2   0b10
custom_c  62   0b00, 0xEC932D5F69181C0
-------------------------------------------
total     128
-------------------------------------------
final: 2489E9AD-2EE2-8E00-8EC9-32D5F69181C0
```
其由三段自定义部分组成，当然各部分都可以自定义。我这里提出我的一些最佳实践。
custom_a：和UUID v7一样的毫秒级时间戳，前面说了可排序性是第一位的
custom_b：seq，12bit的具有业务含义的序列号，支持0-4095总4096个
custom_c：随机数，牺牲一点碰撞性换来更高的易用性

优点：
1. 128bit长，对编程语言来说是长度36字节的字符串，对数据库来说是UUID类型（postgresql）或者128bit binary（mysql）
2. 可排序
3. 随机性强，不可猜测
4. 随机性强，碰撞性极低
5. 可分布式，无依赖
6. 序列号，具有业务可追溯性
缺点：
1. 长度略长
2. 个别数据库支持度不佳
3. 序列号有暴露隐私风险

## ULID
[规范](https://github.com/ulid/spec)
[实现](https://github.com/huxi/sulky/tree/master/sulky-ulid)

## LexicalUUID
[规范](https://github.com/twitter-archive/cassie)
[实现](https://github.com/twitter-archive/cassie/blob/master/cassie-core/src/main/scala/com/twitter/cassie/types/LexicalUUID.scala)

## Snowflake
[](https://github.com/twitter-archive/snowflake)
[](https://github.com/twitter-archive/snowflake/releases/tag/snowflake-2010)

## flake
[](https://github.com/boundary/flake)
[]()

## ShardingID
[](https://instagram-engineering.com/sharding-ids-at-instagram-1cf5a71e5a5c)

## KSUID
[](https://github.com/segmentio/ksuid)


## Elasticflake

## FlakeID

## Sonyflake
[](https://github.com/sony/sonyflake)

## orderedUuid
[](https://itnext.io/laravel-the-mysterious-ordered-uuid-29e7500b4f8)

## COMBGUID

## SID

## pushID

## XID

## ObjectID
[](https://www.mongodb.com/zh-cn/docs/manual/reference/method/ObjectId/)

## CUID
[](https://github.com/paralleldrive/cuid)
