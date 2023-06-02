/*
 * @(#)JmhSplitFloor16.java
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
@Measurement(iterations = 2, time = 1)
@Warmup(iterations = 2, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class JmhSplitFloor16 {
    private static final int TO_MAX = 1024 + 1;
    private static final int FROM_MAX = 100;
    private static final int DATA_LENGTH = TO_MAX - FROM_MAX;

    @Benchmark
    @OperationsPerInvocation(DATA_LENGTH)
    public void oldSplitFloor16(Blackhole blackhole) {
        for (int i = 0; i < FROM_MAX; i++) {
            for (int j = i; j < TO_MAX; j++) {
                blackhole.consume(FastIntegerMathTest.oldSplitFloor16(i, j));
            }
        }
    }
    @Benchmark
    @OperationsPerInvocation(DATA_LENGTH)
    public void splitFloor16(Blackhole blackhole) {
        for (int i = 0; i < FROM_MAX; i++) {
            for (int j = i; j < TO_MAX; j++) {
                blackhole.consume(FastIntegerMath.splitFloor16(i, j));
            }
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhSplitFloor16.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
