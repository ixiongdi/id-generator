package com.github.ixiongdi.id.generator;

import icu.congee.custom.TimeBasedAndBusinessIdGenerator;
import icu.congee.custom.TimeBasedAndRandomIdGenerator;

public class BestPracticeNumberIdGenerator implements NumberIdGenerator {

    public static long timeBasedBusinessId() {
        return TimeBasedAndBusinessIdGenerator.next();
    }

    public static long timeBasedRandomId() {
        return TimeBasedAndRandomIdGenerator.next();
    }

    @Override
    public Number generate() {
        return TimeBasedAndBusinessIdGenerator.next();
    }
}
