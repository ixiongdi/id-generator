package com.github.ixiongdi.id;

import com.github.ixiongdi.id.base.IdGenerator;

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
    }
}
