package icu.congee.id.generator;

import icu.congee.id.generator.util.Clock;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Threads(16)
@Fork(0)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
public class TimeBenchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(TimeBenchmark.class.getSimpleName()).build();
        new Runner(opt).run();
    }

    @Benchmark
    public void now(Blackhole bh) {
        bh.consume(Instant.now());
    }

    @Benchmark
    public void currentTimeMillis(Blackhole bh) {
        bh.consume(System.currentTimeMillis());
    }

    @Benchmark
    public void customTime(Blackhole bh) {
        bh.consume(Clock.currentTimeNanos());
    }

    @Benchmark
    public void nanoTime(Blackhole bh) {
        bh.consume(System.nanoTime());
    }
}
