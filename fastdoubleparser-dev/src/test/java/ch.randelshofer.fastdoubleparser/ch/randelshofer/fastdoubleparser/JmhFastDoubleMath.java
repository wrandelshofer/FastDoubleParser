/*
 * @(#)JmhFastDoubleMath.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;


import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * # JMH version: 1.36
 * # VM version: JDK 20.0.1, OpenJDK 64-Bit Server VM, 20.0.1+9-29
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
 *
 * Benchmark                                       Mode  Cnt  Score   Error  Units
 * JmhFastDoubleMath.tryHexFloatToDoubleTruncated  avgt    4  2.939 ± 0.427  ns/op
 */
@Fork(value = 1, jvmArgsAppend = {"-ea"})
@Measurement(iterations = 4)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public final class JmhFastDoubleMath {
    private boolean negative;
    private long significand;
    private int exponent;

    @Setup
    public void prepare() {
        Random rng = new Random();
        negative = rng.nextBoolean();
        significand = rng.nextLong();
        exponent = rng.nextInt(Double.MIN_EXPONENT, Double.MAX_EXPONENT);
    }

    @Benchmark
    public double tryHexFloatToDoubleTruncated() {
        double v = FastDoubleMath.tryHexFloatToDoubleTruncated(negative, significand, exponent, false, 0);
        assert !Double.isNaN(v);
        return v;
    }
}
