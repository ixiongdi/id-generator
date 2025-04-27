package icu.congee.id.generator.distributed.dtsid;

import icu.congee.id.base.Id;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.text.SimpleDateFormat;
import java.util.Date;

@AllArgsConstructor
@ToString
public class DtsId implements Id {

    private long timestamp;
    private long sequence;

    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    public byte[] toBytes() {
        return long2bytes(toLong());
    }

    @Override
    public long toLong() {
        String formatted = simpleDateFormat.format(new Date(timestamp));
        return Long.parseLong(formatted) * 100000 + sequence % 100000;
    }
}
