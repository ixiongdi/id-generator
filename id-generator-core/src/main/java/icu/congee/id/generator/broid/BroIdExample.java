package icu.congee.id.generator.broid;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/** BroId示例类 展示如何使用BroId生成器创建不同类型的ID */
public class BroIdExample {

    /**
     * 创建一个标准的BroId生成器 结构：48位时间戳 + 16位计数器 + 48位机器标识 + 16位随机数
     *
     * @return BroId生成器
     */
    public static BroIdGenerator<BroId> createSnowflakeGenerator() {
        List<BroIdPart> parts = new ArrayList<>();
        parts.add(new BroIdPart() {
            @Override
            public int getBits() {
                return 41;
            }

            @Override
            public List<Boolean> next() {
                return BitUtils.longToList(System.currentTimeMillis(), this.getBits());
            }
        });
        parts.add(new BroIdPart() {
            @Override
            public int getBits() {
                return 5;
            }

            @Override
            public List<Boolean> next() {
                return BitUtils.longToList(0, this.getBits());
            }
        });
        parts.add(new BroIdPart() {
            @Override
            public int getBits() {
                return 5;
            }

            @Override
            public List<Boolean> next() {
                return BitUtils.longToList(0, this.getBits());
            }
        });
        parts.add(new BroIdPart() {

            private final AtomicLong atomicLong = new AtomicLong(0);

            @Override
            public int getBits() {
                return 12;
            }

            @Override
            public List<Boolean> next() {
                return BitUtils.longToList(atomicLong.getAndIncrement(), this.getBits());
            }
        });
        // 创建布局和生成器
        BroIdLayout layout = new BroIdLayout(parts);
        return new BroIdGenerator<>(layout, BroId::new);
    }

    /**
     * 创建一个标准的BroId生成器 结构：48位时间戳 + 16位计数器 + 48位机器标识 + 16位随机数
     *
     * @return BroId生成器
     */
    public static BroIdGenerator<BroId> createUUIDv8Generator() {
        List<BroIdPart> parts = new ArrayList<>();
        parts.add(new BroIdPart() {
            @Override
            public int getBits() {
                return 64;
            }

            @Override
            public List<Boolean> next() {
                Instant instant = Instant.now();
                long ts = instant.getEpochSecond() * 1000_000_000L + instant.getNano();
                return BitUtils.longToList(ts, this.getBits());
            }
        });
        parts.add(new BroIdPart() {
            @Override
            public int getBits() {
                return 64;
            }

            @Override
            public List<Boolean> next() {
                return BitUtils.longToList(ThreadLocalRandom.current().nextLong(), this.getBits());
            }
        });
        // 创建布局和生成器
        BroIdLayout layout = new BroIdLayout(parts);
        return new BroIdGenerator<>(layout, BroId::new);
    }

    /**
     * 主方法，展示不同生成器的使用
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 创建标准生成器
        BroIdGenerator<BroId> snowflakeGenerator = createSnowflakeGenerator();
        for (int i = 0; i < 10; i++) {
            System.out.println("BroId encode base62: " + snowflakeGenerator.next());
            System.out.println("BroId encode base62: " + snowflakeGenerator.next().toBase62String());
            System.out.println("BroId encode base36: " + snowflakeGenerator.next().toBase36String());
            System.out.println("BroId encode crock ford base32: " + snowflakeGenerator.next().toCrockfordBase32String());
            System.out.println("BroId encode hex: " + snowflakeGenerator.next().toHexString());
            System.out.println("BroId encode long: " + snowflakeGenerator.next().toLong());
        }

        // 创建标准生成器
        BroIdGenerator<BroId> uuidGenerator = createUUIDv8Generator();
        for (int i = 0; i < 10; i++) {
            System.out.println("BroId encode base62: " + uuidGenerator.next());
            System.out.println("BroId encode base62: " + uuidGenerator.next().toBase62String());
            System.out.println("BroId encode base36: " + uuidGenerator.next().toBase36String());
            System.out.println("BroId encode crock ford base32: " + uuidGenerator.next().toCrockfordBase32String());
            System.out.println("BroId encode hex: " + uuidGenerator.next().toHexString());
            System.out.println("BroId encode uuid: " + uuidGenerator.next().toUUID());
        }
    }
}
