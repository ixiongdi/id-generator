package icu.congee.id.generator.broid.part;

import icu.congee.id.generator.broid.BitUtils;
import icu.congee.id.generator.broid.BroIdPart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
public class TimestampBroIdPart implements BroIdPart {

    private final TimeUnit timeUnit;

    private final long epoch;

    private final int bits;

    @Override
    public List<Boolean> next() {
        Instant instant = Instant.now();
        long timestamp;
        switch (timeUnit) {
            case DAYS:
                timestamp = instant.getEpochSecond() / 60 / 60 / 24;
                break;
            case HOURS:
                timestamp = instant.getEpochSecond() / 60 / 60;
                break;
            case MINUTES:
                timestamp = instant.getEpochSecond() / 60;
                break;
            case SECONDS:
                timestamp = instant.getEpochSecond();
                break;
            case MILLISECONDS:
                timestamp = instant.toEpochMilli();
                break;
            case MICROSECONDS:
                timestamp = instant.getEpochSecond() * 1000_000 + instant.getNano() / 1000;
                break;
            case NANOSECONDS:
                timestamp = instant.getEpochSecond() * 1000_000_000 + instant.getNano();
                break;
            default:
                timestamp = System.currentTimeMillis();
                break;
        }

        return BitUtils.longToList(timestamp - epoch, getBits());
    }
}
