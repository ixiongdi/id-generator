package icu.congee.id.generator.distributed.uuid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import icu.congee.id.generator.distributor.MachineIdDistributor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class UUIDv8Generator implements IdGenerator {

    private final MachineIdDistributor machineIdDistributor;
    private final AtomicLong atomicLong = new AtomicLong(0);

    public UUIDv8Generator(RedissonClient redisson) {
        machineIdDistributor = new MachineIdDistributor(redisson, IdType.UUIDv8.getName(), 48);
    }

    @Override
    public UUID generate() {
        Instant now = Instant.now();
        long timestamp = (now.getEpochSecond() * 1000_000_000L + now.getNano()) / 10;
        long clockSeq = atomicLong.getAndUpdate(x -> {
            if (x >= 1 << 14) {
                return 0;
            } else {
                return x + 1;
            }
        });
        long node = machineIdDistributor.get();
        return new UUID(timestamp, clockSeq, node);
    }

    @Override
    public IdType idType() {
        return IdType.UUIDv8;
    }
}
