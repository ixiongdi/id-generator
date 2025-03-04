package com.github.ixiongdi.id.generator;

import icu.congee.ksuid.KsuidGenerator;

public class KSUIDGenerator implements StringIdGenerator {
    @Override
    public String generate() {
        return KsuidGenerator.generate();
    }
}
