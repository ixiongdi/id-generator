package com.github.ixiongdi.id.generator.LexicalUUID;

public class MicrosecondEpochClock extends StrictlyIncreasingClock {
    // 单例模式实现（对应Scala的object）
    private static final MicrosecondEpochClock INSTANCE = new MicrosecondEpochClock();
    
    // 私有构造函数防止外部实例化
    private MicrosecondEpochClock() {}
    
    public static MicrosecondEpochClock getInstance() {
        return INSTANCE;
    }

    // 实现抽象方法（对应Scala的protected def tick）
    @Override
    protected long tick() {
        return System.currentTimeMillis() * 1000L; 
    }
}