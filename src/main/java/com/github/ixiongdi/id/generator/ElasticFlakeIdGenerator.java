package com.github.ixiongdi.id.generator;

import icu.congee.elasticflake.TimeBasedUUIDGenerator;

public class ElasticFlakeIdGenerator implements StringIdGenerator {

    TimeBasedUUIDGenerator timeBasedUUIDGenerator = new TimeBasedUUIDGenerator();

    @Override
    public String generate() {
        return timeBasedUUIDGenerator.getBase64UUID();
    }
}
