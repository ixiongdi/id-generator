package uno.xifan.id.generator.distributed.future;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

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
