package icu.congee.id.generator.cuid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

public class CUIDv1Generator implements IdGenerator {
    @Override
    public String generate() {
        return CUID.randomCUID1().toString();
    }

    @Override
    public IdType idType() {
        return IdType.CUIDv1;
    }
}
