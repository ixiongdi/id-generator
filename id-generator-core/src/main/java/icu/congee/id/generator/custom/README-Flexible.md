# 灵活的ID生成器

## 使用方法

### 使用List<IdPart>创建灵活的长整型ID生成器

```java
// 创建ID部分列表
List<IdPart> idParts = new ArrayList<>();

// 添加时间戳部分（41位）
TimestampPart timestampPart = new TimestampPart(41);
idParts.add(timestampPart);

// 添加工作节点ID部分（10位）
WorkerIdPart workerIdPart = new WorkerIdPart(10, 5); // 使用固定值5
idParts.add(workerIdPart);

// 添加序列号部分（12位）
SequencePart sequencePart = new SequencePart(12);
idParts.add(sequencePart);

// 添加随机数部分（1位）
RandomPart randomPart = new RandomPart(1);
idParts.add(randomPart);

// 创建灵活的长整型ID生成器
LongIdGenerator idGenerator = new FlexibleLongIdGenerator(idParts);

// 生成ID
Long id = idGenerator.generate();
```

### 使用List<IdPart>和名称列表创建灵活的长整型ID生成器

```java
// 创建ID部分列表
List<IdPart> idParts = new ArrayList<>();
idParts.add(new TimestampPart(41));
idParts.add(new WorkerIdPart(10, 5));
idParts.add(new SequencePart(12));
idParts.add(new RandomPart(1));

// 创建ID部分名称列表
List<String> partNames = new ArrayList<>();
partNames.add("timestamp");
partNames.add("workerId");
partNames.add("sequence");
partNames.add("random");

// 创建灵活的长整型ID生成器
LongIdGenerator idGenerator = new FlexibleLongIdGenerator(idParts, partNames);

// 生成ID
Long id = idGenerator.generate();

// 使用格式化生成ID
Long formattedId = idGenerator.generateWithFormat("{ts}{wid}{seq}{rnd}");

// 获取各部分的值
long timestampValue = idGenerator.getTimestampPart();
long workerIdValue = idGenerator.getWorkerIdPart();
long sequenceValue = idGenerator.getSequencePart();
long randomValue = idGenerator.getRandomPart();
```

### 使用Map<String, IdPart>创建灵活的长整型ID生成器

```java
// 创建ID部分映射
Map<String, IdPart> partMap = new HashMap<>();
partMap.put("timestamp", new TimestampPart(41));
partMap.put("workerId", new WorkerIdPart(10, 5));
partMap.put("sequence", new SequencePart(12));
partMap.put("random", new RandomPart(1));

// 创建灵活的长整型ID生成器
FlexibleLongIdGenerator idGenerator = new FlexibleLongIdGenerator(partMap);

// 生成ID
Long id = idGenerator.generate();

// 获取指定名称的ID部分值
long timestampValue = idGenerator.getPartValue("timestamp");

// 获取指定名称的ID部分字节数组
byte[] timestampBytes = idGenerator.getPartBytes("timestamp");
```

### 自定义ID部分顺序

通过调整ID部分列表中元素的顺序，可以自定义ID各部分的顺序：

```java
// 创建ID部分列表，自定义顺序：工作节点ID -> 序列号 -> 时间戳 -> 随机数
List<IdPart> idParts = new ArrayList<>();
idParts.add(new WorkerIdPart(10, 5));  // 第一部分
idParts.add(new SequencePart(12));     // 第二部分
idParts.add(new TimestampPart(41));    // 第三部分
idParts.add(new RandomPart(1));        // 第四部分

// 创建灵活的长整型ID生成器
LongIdGenerator idGenerator = new FlexibleLongIdGenerator(idParts);

// 生成ID
Long id = idGenerator.generate();
```

### 自定义ID部分包含与否

通过选择性地添加ID部分到列表中，可以控制ID中包含哪些部分：

```java
// 创建ID部分列表，只包含时间戳和序列号
List<IdPart> idParts = new ArrayList<>();
idParts.add(new TimestampPart(52));    // 分配更多位给时间戳
idParts.add(new SequencePart(12));     // 序列号保持12位

// 创建灵活的长整型ID生成器
LongIdGenerator idGenerator = new FlexibleLongIdGenerator(idParts);

// 生成ID
Long id = idGenerator.generate();
```

### 获取ID部分的字节数组表示

```java
// 创建灵活的长整型ID生成器
FlexibleLongIdGenerator idGenerator = new FlexibleLongIdGenerator(idParts, partNames);

// 生成ID
idGenerator.generate();

// 获取各部分的字节数组表示
byte[] timestampBytes = ((FlexibleLongIdGenerator) idGenerator).getPartBytes("timestamp");
byte[] workerIdBytes = ((FlexibleLongIdGenerator) idGenerator).getPartBytes("workerId");
byte[] sequenceBytes = ((FlexibleLongIdGenerator) idGenerator).getPartBytes("sequence");
byte[] randomBytes = ((FlexibleLongIdGenerator) idGenerator).getPartBytes("random");
```