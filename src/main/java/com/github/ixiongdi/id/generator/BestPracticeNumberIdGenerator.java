package com.github.ixiongdi.id.generator;

import com.github.ixiongdi.id.generator.custom.TimeBasedAndBusinessIdGenerator;
import com.github.ixiongdi.id.generator.custom.TimeBasedAndRandomIdGenerator;

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
