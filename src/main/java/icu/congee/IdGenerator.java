package icu.congee;

public interface IdGenerator {
    // 生成单个ID，返回Object以支持不同类型
    Object generate();
}