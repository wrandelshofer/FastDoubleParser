/*
 * @(#)JmhScalb.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * # JMH version: 1.36
 * # VM version: JDK 20, OpenJDK 64-Bit Server VM, 20+36-2344
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 * </pre>
 * (str)  Mode  Cnt    Score   Error  Units
 * 0x123.456789abcdep123  avgt    2  368.869          ns/op
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 16, OpenJDK 64-Bit Server VM, 16+36-2231
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark              Mode  Cnt   Score   Error  Units
 * JmhScalb.mCustomScalb  avgt    4  34.201 ± 0.610  ns/op
 * JmhScalb.mMathScalb    avgt    4   1.892 ± 0.449  ns/op
 * JmhScalb.mMathScalb1   avgt    4   1.333 ± 0.024  ns/op
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector",
        "--enable-preview",

        // Options for analysis with https://github.com/AdoptOpenJDK/jitwatch
        //"-XX:+UnlockDiagnosticVMOptions",
        //"-Xlog:class+load=info",
        //"-XX:+LogCompilation",
        //"-XX:+PrintAssembly"
})
@Measurement(iterations = 4)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhScalb {
    double d;
    int scaleFactor;

    @Setup
    public void prepare() {
        Random rng = new Random();
        d = rng.nextDouble();
        scaleFactor = rng.nextInt(Double.MIN_EXPONENT, Double.MAX_EXPONENT);
    }

    @Benchmark
    public double mMathScalb() {
        return Math.scalb(d, scaleFactor);
    }

    @Benchmark
    public double mMathScalb1() {
        return d * Math.scalb(1, scaleFactor);
    }

    @Benchmark
    public double mCustomScalb() {
        return customScalb(d, scaleFactor);
    }

    public static final int DOUBLE_EXPONENT_BIAS = 1023;
    /**
     * The number of bits in the significand, including the implicit bit.
     */
    public static final int DOUBLE_SIGNIFICAND_WIDTH = 53;

    static double customScalb(double number, int scaleFactor) {
        return number * Double.longBitsToDouble((scaleFactor + DOUBLE_EXPONENT_BIAS) << (DOUBLE_SIGNIFICAND_WIDTH - 1));
    }
}
