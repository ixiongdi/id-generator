package icu.congee.id;

import icu.congee.id.base.IdGenerator;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ServiceLoader;

public class Main {

    public static void main(String[] args) throws NoSuchAlgorithmException {

        SecureRandom instance = SecureRandom.getInstanceStrong();
        instance.nextLong();

        ServiceLoader<IdGenerator> loader = ServiceLoader.load(IdGenerator.class);

        for (IdGenerator generator : loader) {
            for (int i = 0; i < 10; i++) {
                System.out.printf("%s: ", generator.idType());
                System.out.println(generator.generate());
            }
        }
    }
}
