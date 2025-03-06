package com.github.ixiongdi.id.generator;

public enum IdType {
    CUSTOM_TIME_BASED_BUSINESS_ID("CustomTimeBasedBusinessId"),
    CUSTOM_TIME_BASED_RANDOM_ID("CustomTimeBasedRandomId"),
    ;

    private final String name;

    IdType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
