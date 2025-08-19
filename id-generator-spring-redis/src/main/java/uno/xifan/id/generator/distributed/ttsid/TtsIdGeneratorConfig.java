package uno.xifan.id.generator.distributed.ttsid;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class TtsIdGeneratorConfig {
    @Value("${IdGenerator.TtsId.namespace:default}")
    private String namespace = "default";

    @Value("${IdGenerator.TtsId.timestampBits:41}")
    private Integer timestampBits = 41;

    @Value("${IdGenerator.TtsId.timestampBits:10}")
    private Integer threadIdBits = 10;

    @Value("${IdGenerator.TtsId.timestampBits:12}")
    private Integer sequenceBits = 12;
}
