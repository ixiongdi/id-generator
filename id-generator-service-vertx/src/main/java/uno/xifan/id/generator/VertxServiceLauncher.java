package uno.xifan.id.generator;

import uno.xifan.id.util.IdUtil;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;

public class VertxServiceLauncher extends VerticleBase {
    public Future<?> start() {
        return vertx.createHttpServer()
                .requestHandler(req ->
                        req.response()
                                .putHeader("content-type", "text/plain")
                                .end(IdUtil.uuid7())
                )
                .listen(8080);
    }

}