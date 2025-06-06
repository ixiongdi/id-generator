package icu.congee.id.generator.distributed.dtsid;

import icu.congee.id.base.Id;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@ToString
public class DtsId implements Id {

    private static final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private long timestamp;
    private long sequence;

    @Override
    public byte[] toBytes() {
        return long2bytes(toLong());
    }

    @Override
    public long toLong() {
        String formatted =
                LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault())
                        .format(dateTimeFormatter);
        return Long.parseLong(formatted) * 100000 + sequence % 100000;
    }
}
