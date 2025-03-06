package com.github.ixiongdi.id.core;

public enum IdType {
    CUSTOM_TIME_BASED_BUSINESS_ID("CustomTimeBasedBusinessId"),
    CUSTOM_TIME_BASED_RANDOM_ID("CustomTimeBasedRandomId"),
    MIST_FAST_ID("MistFastId"),
    MIST_DEFAULT_ID("MistDefaultId"),
    MIST_SECURE_ID("MistSecureId"),
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
