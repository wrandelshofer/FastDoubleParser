/*
 * @(#)JmhOrWithinForArrayIndex.java
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
public class JmhOrWithinForArrayIndex {

    public int index = 10;

    @Benchmark
    public void getUsingPlus(Blackhole blackhole) {
        blackhole.consume(getUsingPlus(index));
    }

    @Benchmark
    public void getUsingBitwiseOr(Blackhole blackhole) {
        blackhole.consume(getUsingBitwiseOr(index));
    }
    private static final int IMAG = 1;

    private final double[] a = new double[20];

    private double getUsingPlus(int offa) {
        return a[offa + IMAG];
    }

    private double getUsingBitwiseOr(int offa) {
        return a[offa | IMAG];
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhOrWithinForArrayIndex.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
