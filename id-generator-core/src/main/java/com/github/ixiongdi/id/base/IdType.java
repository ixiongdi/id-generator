package com.github.ixiongdi.id.base;

public enum IdType {
    CUSTOM_TIME_BASED_BUSINESS_ID("CustomTimeBasedBusinessId"),
    CUSTOM_TIME_BASED_RANDOM_ID("CustomTimeBasedRandomId"),
    MIST_FAST_ID("MistFastId"),
    MIST_DEFAULT_ID("MistDefaultId"),
    MIST_SECURE_ID("MistSecureId"),
    UUID_VERSION_1("UUIDVersion1"),
    UUID_VERSION_2("UUIDVersion2"),
    UUID_VERSION_3("UUIDVersion3"),
    UUID_VERSION_4("UUIDVersion4"),
    UUID_VERSION_5("UUIDVersion5"),
    UUID_VERSION_6("UUIDVersion6"),
    UUID_VERSION_7("UUIDVersion7"),
    UUID_VERSION_8("UUIDVersion8"),
    NANO_ID("NanoId"),
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
