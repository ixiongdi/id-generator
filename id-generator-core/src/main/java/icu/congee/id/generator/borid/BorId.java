package icu.congee.id.generator.borid;

import icu.congee.id.base.Base62Codec;

import java.util.Arrays;

/**
 * BorId值对象
 * 包含字节数组值，重写toString方法返回Base62编码值
 */
public class BorId {
    
    private final byte[] value;
    
    /**
     * 构造函数
     * 
     * @param value 字节数组值
     */
    public BorId(byte[] value) {
        this.value = value;
    }
    
    /**
     * 获取字节数组值
     * 
     * @return 字节数组
     */
    public byte[] getValue() {
        return value;
    }
    
    /**
     * 重写toString方法，返回Base62编码值
     * 
     * @return Base62编码的字符串
     */
    @Override
    public String toString() {
        return Base62Codec.encode(value);
    }
    
    /**
     * 重写equals方法
     * 
     * @param o 比较对象
     * @return 是否相等
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorId borId = (BorId) o;
        return Arrays.equals(value, borId.value);
    }
    
    /**
     * 重写hashCode方法
     * 
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}