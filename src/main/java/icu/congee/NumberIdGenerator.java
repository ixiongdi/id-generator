package icu.congee;

public interface NumberIdGenerator extends IdGenerator {
    @Override
    Number generate();
}
