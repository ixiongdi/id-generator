# MIST_ID 算法

## 算法简介

MIST_ID（薄雾算法）是一种高性能的分布式 ID 生成方案，通过结合自增数和随机因子来生成唯一标识符。该算法设计简洁，易于实现，同时提供了良好的性能和可扩展性。

## ID 结构

### 标准实现

MIST_ID 的标准实现采用 64 位长整型，结构如下：

```
[自增数部分][随机因子A][随机因子B]
```

- 自增数部分：占据高位，通过 AtomicLong 实现原子递增
- 随机因子 A：8 位，取值范围 0-255
- 随机因子 B：8 位，取值范围 0-255

通过位运算将这三部分组合在一起，形成最终的 ID。

### 分布式实现

MIST_ID 的分布式实现基于 Redis 的 RAtomicLong，结构如下：

```
[自增数部分][随机数部分]
```

- 自增数部分：通过 Redis 的 RAtomicLong 实现分布式递增
- 随机数部分：16 位，通过随机数生成器生成

## 算法特点

### 优点

1. **高性能**：标准实现使用 AtomicLong 和 ThreadLocalRandom，分布式实现采用预填充队列机制，都能提供极高的性能
2. **简单易用**：算法实现简单，易于理解和维护
3. **分布式友好**：提供基于 Redis 的分布式实现，支持多节点部署
4. **ID 唯一性**：通过自增数和随机因子的组合，保证 ID 的唯一性
5. **安全性选项**：分布式实现支持使用 SecureRandom 提高随机性安全性

### 缺点

1. **依赖性**：分布式实现依赖 Redis 服务
2. **时间特性**：不包含时间信息，不支持按时间排序
3. **ID 长度**：生成的 ID 为纯数字，可能较长

## 适用场景

- 需要高性能 ID 生成的分布式系统
- 对 ID 生成速度有较高要求的应用
- 不需要 ID 包含时间信息的场景
- 需要在分布式环境下保证 ID 唯一性的系统

## 代码实现

### 标准实现

```java
public class StandardMistGenerator implements MistGenerator {
    private static final int SALT_BIT = 8; // 随机因子二进制位数
    private static final int SALT_SHIFT = 8; // 随机因子移位数
    private static final int INCREAS_SHIFT = SALT_BIT + SALT_SHIFT; // 自增数移位数
    private static final int MAX_SALT_VALUE = 255; // 随机因子最大值

    private final AtomicLong increas = new AtomicLong(1); // 自增数
    private final ThreadLocalRandom random = ThreadLocalRandom.current(); // 线程安全的随机数生成器

    @Override
    public Long generate() {
        // 自增
        long increasValue = increas.incrementAndGet();

        // 获取随机因子数值
        long saltA = random.nextInt(MAX_SALT_VALUE + 1);
        long saltB = random.nextInt(MAX_SALT_VALUE + 1);

        // 通过位运算实现自动占位
        return (increasValue << INCREAS_SHIFT) | (saltA << SALT_SHIFT) | saltB;
    }
}
```

### 分布式实现

```java
@Component
public class MistIdGenerator implements IdGenerator {

    private final Queue<Long> queue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    @Resource
    private RedissonClient redisson;
    private RAtomicLong atomicLong;

    // 配置参数
    @Value("${id.generator.mist.name:IdGenerator:AtomicLongIdGenerator:current}")
    private String name;
    @Value("${id.generator.mist.value:-1}")
    private Long value;
    @Value("${id.generator.mist.secret:false}")
    private Boolean secret;
    @Value("${id.generator.mist.bufferSize:65536}")
    private Integer bufferSize;
    private Random random;

    @PostConstruct
    public void init() {
        atomicLong = redisson.getAtomicLong(name);
        if (value >= 0) {
            atomicLong.set(value);
        }
        random = secret ? new SecureRandom() : ThreadLocalRandom.current();

        // 客户端心跳线程，定期填充队列
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::fillQueue, 0, 10, TimeUnit.MILLISECONDS);
        fillQueue();
    }

    private void fillQueue() {
        long e = atomicLong.getAndAdd(bufferSize);
        for (int i = 0; i < bufferSize; i++) {
            queue.offer(e + i);
        }
    }

    @Override
    public Long generate() {
        synchronized (this) {
            if (queue.size() < bufferSize * 0.1 && isFilling.compareAndSet(false, true)) {
                new Thread(() -> {
                    try {
                        fillQueue();
                    } finally {
                        isFilling.set(false);
                    }
                }).start();
            }
        }
        return queue.remove() | random.nextInt() & 0xFFFF;
    }
}
```

## 使用示例

### 标准实现

```java
// 获取标准实现的单例实例
StandardMistGenerator generator = StandardMistGenerator.getInstance();

// 生成ID
Long id = generator.generate();
System.out.println("生成的ID: " + id);
```

### 分布式实现

```java
// 注入分布式实现
@Resource
private MistIdGenerator mistIdGenerator;

// 生成ID
Long id = mistIdGenerator.generate();
System.out.println("生成的分布式ID: " + id);
```

## 性能考虑

1. **标准实现**：使用 AtomicLong 和 ThreadLocalRandom，性能极高，适合单机环境
2. **分布式实现**：
   - 使用预填充队列机制，减少 Redis 访问频率
   - 当队列容量低于阈值时自动填充，保证 ID 生成的高效性
   - 支持配置缓冲区大小，可根据实际需求调整
   - 提供安全随机选项，可在安全性和性能之间进行权衡

## 配置参数（分布式实现）

| 参数名                       | 默认值                                    | 说明                  |
| ---------------------------- | ----------------------------------------- | --------------------- |
| id.generator.mist.name       | IdGenerator:AtomicLongIdGenerator:current | Redis 中存储的键名    |
| id.generator.mist.value      | -1                                        | 初始值，-1 表示不设置 |
| id.generator.mist.secret     | false                                     | 是否使用安全随机数    |
| id.generator.mist.bufferSize | 65536                                     | 缓冲区大小            |
