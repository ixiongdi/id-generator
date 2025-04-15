package icu.congee.id.generator.distributor;

import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RSetCache;
import org.redisson.api.RedissonClient;

import java.math.BigInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MachineIdDistributor {

    // 记录已分配的机器ID
    private final RSetCache<Long> set;
    private final long machineId;

    public MachineIdDistributor(RedissonClient redisson, String namespace) {
        String name = String.format("IdGenerator:MachineIdService:%s:Set", namespace);
        this.set = redisson.getSetCache(name);
        this.machineId = register();

        // 客户端心跳线程
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::renewLease, 10, 20, TimeUnit.SECONDS); // 每25秒续期一次
    }

    public void renewLease() {
        set.add(machineId, 60, TimeUnit.SECONDS);
        log.info("renew machine id:{}", machineId);
    }

    public long register() {
        long id = 0L;
        while (set.contains(id)) {
            id++;
        }
        set.add(id, 60, TimeUnit.SECONDS);
        log.info("register machine id:{}", id);
        return id;
    }

    public long get() {
        return machineId;
    }
}
