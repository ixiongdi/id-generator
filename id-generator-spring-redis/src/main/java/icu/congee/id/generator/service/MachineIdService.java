package icu.congee.id.generator.service;

import org.redisson.api.RMapCache;
import org.redisson.api.RSetCache;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class MachineIdService {

    // 记录实例和机器ID的关系
    private final RMapCache<String, Long> map;
    // 记录已分配的机器ID
    private final RSetCache<Long> set;

    public MachineIdService(RedissonClient redisson, String namespace) {
        this.map = redisson.getMapCache("IdGenerator:MachineIdService:%s:Map".formatted(namespace));
        this.set = redisson.getSetCache("IdGenerator:MachineIdService:%s:Set".formatted(namespace));
    }

    public Long get(String uuid) {
        if (!map.containsKey(uuid)) {
            AtomicLong atomicLong = new AtomicLong(0);
            while (set.contains(atomicLong.get())) {
                atomicLong.incrementAndGet();
            }
            set.add(atomicLong.get(), 1, TimeUnit.MINUTES);
            map.put(uuid, atomicLong.get(), 1, TimeUnit.MINUTES);
        }
        return map.get(uuid);
    }
}
