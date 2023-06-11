/*
 * @(#)JmhMaskLowestBytes.java
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
public class JmhMaskLowestBytes {

    public int intNumber = 100000;

    public long longNumber = 1034434L;

    @Benchmark
    public void intMaskedViaAnd(Blackhole blackhole) {
        blackhole.consume(intMaskedViaAnd(intNumber));
    }

    @Benchmark
    public void intMaskedViaShift(Blackhole blackhole) {
        blackhole.consume(intMaskedViaShift(intNumber));
    }

    @Benchmark
    public void longMaskedViaAnd(Blackhole blackhole) {
        blackhole.consume(longMaskedViaAnd(longNumber));
    }

    @Benchmark
    public void longMaskedViaShift(Blackhole blackhole) {
        blackhole.consume(longMaskedViaShift(longNumber));
    }


    private int intMaskedViaAnd(int number) {
        return number & 0xff;
    }

    private int intMaskedViaShift(int number) {
        return number << 24 >>> 8;
    }

    private long longMaskedViaAnd(long number) {
        return number & 0xffffffffL;
    }

    private long longMaskedViaShift(long number) {
        return number << 32 >>> 32;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhMaskLowestBytes.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
