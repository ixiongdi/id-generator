package uno.xifan.id.generator.service.solon;

import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;

@SolonMain
public class IdGeneratorServiceSolonApplication {
    public static void main(String[] args) {
        Solon.start(IdGeneratorServiceSolonApplication.class, args);
    }
}