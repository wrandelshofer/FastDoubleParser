/*
 * @(#)JmhOrWithinIndex.java
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
public class JmhOrWithinIndex {

    @Param({"41", "18"})
    public int index;

    public int offset = 4;
    public static final int COMPLEX_SIZE_SHIFT = 1;
    public static final int IMAG = 1;
    @Benchmark
    public void plus(Blackhole blackhole) {
        blackhole.consume(imagIdxPlus(index));
    }

    @Benchmark
    public void bitwiseOr(Blackhole blackhole) {
        blackhole.consume(imagIdxOr(index));
    }
    private int imagIdxPlus(int idxa) {
        return (idxa << COMPLEX_SIZE_SHIFT) + offset + IMAG;
    }

    private int imagIdxOr(int idxa) {
        return (idxa << COMPLEX_SIZE_SHIFT) + offset | IMAG;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhOrWithinIndex.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
