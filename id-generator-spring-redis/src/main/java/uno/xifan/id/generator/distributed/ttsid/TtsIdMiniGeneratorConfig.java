package uno.xifan.id.generator.distributed.ttsid;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class TtsIdMiniGeneratorConfig {
    @Value("${IdGenerator.TtsIdMini.namespace:default}")
    private String namespace = "default";

    @Value("${IdGenerator.TtsIdMini.timestampBits:31}")
    private Integer timestampBits = 31;

    @Value("${IdGenerator.TtsIdMini.timestampBits:10}")
    private Integer threadIdBits = 10;

    @Value("${IdGenerator.TtsIdMini.timestampBits:12}")
    private Integer sequenceBits = 12;
}
