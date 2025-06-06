package icu.congee.id.generator.ksuid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

public class KsuidGenerator implements IdGenerator {

    public static String next() {
        return KsuidCreator.getKsuid().toString();
    }

    @Override
    public String generate() {
        return KsuidCreator.getKsuid().toString();
    }

    @Override
    public IdType idType() {
        return IdType.KSUID;
    }
}
