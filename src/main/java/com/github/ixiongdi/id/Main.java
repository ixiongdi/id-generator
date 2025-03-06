package com.github.ixiongdi.id;

import com.github.ixiongdi.id.core.IdGenerator;

import java.util.ServiceLoader;

public class Main {

    public static void main(String[] args) {

        ServiceLoader<IdGenerator> loader = ServiceLoader.load(IdGenerator.class);

        for (IdGenerator generator : loader) {

            for (int i = 0; i < 10; i++) {
                System.out.printf("%s: ", generator.idType());
                System.out.println(generator.generate());
            }
        }

        // 示例 long 值
        long longValue = 123456789012345L;

        // 将 long 值转换为十六进制字符串
        String hexString = Long.toHexString(longValue);

        // 打印结果
        System.out.println("Original long value: " + longValue);
        System.out.println("Hex string: " + hexString);
    }
}
