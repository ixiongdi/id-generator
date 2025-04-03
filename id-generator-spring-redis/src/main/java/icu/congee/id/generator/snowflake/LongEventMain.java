package icu.congee.id.generator.snowflake;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

public class LongEventMain {
    public static void main(String[] args) throws Exception {
        int bufferSize = 1024;

        Disruptor<LongEvent> disruptor =
                new Disruptor<>(LongEvent::new, bufferSize, DaemonThreadFactory.INSTANCE);

        disruptor.handleEventsWith((event, sequence, endOfBatch) -> {});
        disruptor.start();

        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        for (long l = 0; true; l++) {
            ringBuffer.publishEvent((event, sequence) -> {});
            Thread.sleep(1);
        }
    }
}
