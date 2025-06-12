package icu.congee.id.generator.distributed.ttsid;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class TtsIdMiniGeneratorConfig {
    @Value("${IdGenerator.TtsIdMini.namespace:default}")
    private int namespace;

    @Value("${IdGenerator.TtsIdMini.timestampBits:31}")
    private int timestampBits;

    @Value("${IdGenerator.TtsIdMini.timestampBits:10}")
    private int threadIdBits;

    @Value("${IdGenerator.TtsIdMini.timestampBits:12}")
    private int sequenceBits;
}
