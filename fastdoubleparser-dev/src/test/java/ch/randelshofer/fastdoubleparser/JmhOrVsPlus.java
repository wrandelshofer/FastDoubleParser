/*
 * @(#)JmhOrVsPlus.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Fork(value = 1)
@Measurement(iterations = 10, time = 1)
@Warmup(iterations = 2, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhOrVsPlus {

    public long high = 1034434L;
    public long low = 1034434L;

    @Benchmark
    public void plus(Blackhole blackhole) {
        blackhole.consume(plus(high, low));
    }

    @Benchmark
    public void bitwiseOr(Blackhole blackhole) {
        blackhole.consume(bitwiseOr(high, low));
    }

    private long plus(long high, long low) {
        return (high << 32) + low;
    }

    private long bitwiseOr(long high, long low) {
        return (high << 32) | low;
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhOrVsPlus.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
