package com.github.ixiongdi.id.core;

import com.github.ixiongdi.id.generator.custom.TimeBasedRandomIdGenerator;

import java.util.ServiceLoader;

public interface IdGenerator {

    default Object generate() {
        return TimeBasedRandomIdGenerator.next();
    }

    IdType idType();

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
