package com.github.ixiongdi.id.generator;

public interface IdGenerator {
    Object generate();

    IdType idType();
}
