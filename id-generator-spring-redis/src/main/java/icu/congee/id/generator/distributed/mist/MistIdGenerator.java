package icu.congee.id.generator.distributed.mist;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import org.redisson.api.RIdGenerator;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MistIdGenerator implements IdGenerator {

    private final Random random;
    private final RIdGenerator generator;

    public MistIdGenerator(RedissonClient redisson, @Value("${id.generator.mist.name:IdGenerator:MistIdGenerator:current}") String name, @Value("${id.generator.mist.value:-1}") long initialValue, @Value("${id.generator.mist.secret:false}") boolean useSecureRandom, @Value("${id.generator.mist.bufferSize:1000}") int bufferSize) {
        this.random = useSecureRandom ? new SecureRandom() : ThreadLocalRandom.current();
        this.generator = redisson.getIdGenerator(name);
        this.generator.tryInit(initialValue, bufferSize);
    }


    @Override
    public MistId generate() {
        return new MistId(generator.nextId(), random.nextInt(0, 65535));
    }

    @Override
    public IdType idType() {
        return IdType.MIST_ID;
    }

}