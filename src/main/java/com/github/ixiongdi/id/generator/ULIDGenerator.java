package com.github.ixiongdi.id.generator;

import icu.congee.ulid.ULID;

public class ULIDGenerator implements StringIdGenerator {

    private final ULID ulid = new ULID();

    @Override
    public String generate() {
        return ulid.nextULID();
    }
}
