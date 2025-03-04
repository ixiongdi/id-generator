package com.github.ixiongdi.id.generator;

public interface NumberIdGenerator extends IdGenerator {
    @Override
    Number generate();
}
