package com.github.ixiongdi.id.generator;

import icu.congee.LexicalUUID.LexicalUUID;
import icu.congee.LexicalUUID.MicrosecondEpochClock;

public class LexicalUUIDGenerator implements StringIdGenerator {
    @Override
    public String generate() {
        return new LexicalUUID(MicrosecondEpochClock.getInstance()).toString();
    }
}
