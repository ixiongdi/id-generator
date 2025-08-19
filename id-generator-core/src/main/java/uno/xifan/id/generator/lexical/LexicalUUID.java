package uno.xifan.id.generator.lexical;

import uno.xifan.id.base.Base16;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class LexicalUUID implements Comparable<LexicalUUID> {
    private static final long defaultWorkerID;

    static {
        try {
            defaultWorkerID = FNV1A.hash(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private final long timestamp;
    private final long workerID;

    public LexicalUUID(long timestamp, long workerID) {
        this.timestamp = timestamp;
        this.workerID = workerID;
    }

    public LexicalUUID(Clock clock, long workerID) {
        this(clock.timestamp(), workerID);
    }

    public LexicalUUID(Clock clock) {
        this(clock.timestamp(), defaultWorkerID);
    }

    public static LexicalUUID fromClock(Clock clock) {
        return new LexicalUUID(clock, defaultWorkerID);
    }

    public static LexicalUUID fromString(String uuid) {
        byte[] bytes = Base16.decode(uuid.replaceAll("-", ""));
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long timestamp = buffer.getLong();
        long workerID = buffer.getLong();
        return new LexicalUUID(timestamp, workerID);
    }

    @Override
    public int compareTo(LexicalUUID that) {
        int res = Long.compare(this.timestamp, that.timestamp);
        if (res == 0) {
            return Long.compare(this.workerID, that.workerID);
        } else {
            return res;
        }
    }

    @Override
    public String toString() {
        String hex = String.format("%016x", timestamp);
        return String.format(
                "%s-%s-%s-%016x",
                hex.substring(0, 8), hex.substring(8, 12), hex.substring(12, 16), workerID);
    }
}
