package icu.congee.id.generator.distributed.future;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import java.util.concurrent.Future;

public class FutureIdGenerator implements IdGenerator {

    @Override
    public Future<Long> generate() {
        return null;
    }

    @Override
    public IdType idType() {
        return null;
    }
}
