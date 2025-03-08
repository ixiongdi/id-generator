package icu.congee.id.base;

import icu.congee.id.generator.custom.TimeBasedRandomIdGenerator;

import java.util.ServiceLoader;

public interface IdGenerator {

    default Object generate() {
        return TimeBasedRandomIdGenerator.next();
    }

    default IdType idType() {
        return IdType.valueOf(this.getClass().getSimpleName());
    };

    default IdGenerator getInstance(IdType idType) {
        ServiceLoader<IdGenerator> loader = ServiceLoader.load(IdGenerator.class);
        while (loader.iterator().hasNext()) {
            IdGenerator generator = loader.iterator().next();
            if (idType == generator.idType()) {
                return generator;
            }
        }
        return this;
    }
}
