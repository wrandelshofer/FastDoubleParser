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
@Measurement(iterations = 4, time = 1)
@Warmup(iterations = 2, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhScalb {
    double d;
    float f;
    long i;
    int scaleFactorD;

    int scaleFactorF;

    @Setup
    public void prepare() {
        Random rng = new Random();
        d = rng.nextDouble();
        i = (long)(d * 120423423423L);
        f = (float) d;
        scaleFactorD = rng.nextInt(Double.MIN_EXPONENT, Double.MAX_EXPONENT);
        scaleFactorF = rng.nextInt(Float.MIN_EXPONENT, Float.MAX_EXPONENT);
    }

    @Benchmark
    public double mMathScalb() {
        return Math.scalb(d, scaleFactorD);
    }

    @Benchmark
    public double mMathScalb1() {
        return d * Math.scalb(1, scaleFactorD);
    }

    @Benchmark
    public double mCustomScalb() {
        return customScalb(d, scaleFactorD);
    }

    @Benchmark
    public double mCustomScalbLong() {
        return customScalbLong(d, scaleFactorD);
    }

    public static final int DOUBLE_EXPONENT_BIAS = 1023;
    /**
     * The number of bits in the significand, including the implicit bit.
     */
    public static final int DOUBLE_SIGNIFICAND_WIDTH = 53;

    // very strange that this is 30x slower than its sibling customScalbLong
    static double customScalb(double number, int scaleFactor) {
        return number * Double.longBitsToDouble((scaleFactor + DOUBLE_EXPONENT_BIAS) << (DOUBLE_SIGNIFICAND_WIDTH - 1));
    }

    static double customScalbLong(double number, long scaleFactor) {
        return number * Double.longBitsToDouble((scaleFactor + DOUBLE_EXPONENT_BIAS) << (DOUBLE_SIGNIFICAND_WIDTH - 1));
    }

    @Benchmark
    public double mMathScalbFloat() {
        return Math.scalb(f, scaleFactorF);
    }

    @Benchmark
    public double mCustomScalbFloat() {
        return customScalbInt(f, scaleFactorF);
    }

    @Benchmark
    public double mCustomScalbFloat2() {
        return customScalbInt2(i, true, scaleFactorF);
    }

    static float customScalbInt(float number, int scaleFactor) {
        return number * Float.intBitsToFloat((scaleFactor + FLOAT_EXPONENT_BIAS) << (FLOAT_SIGNIFICAND_WIDTH - 1));
    }

    // significand is not converted to float before
    static float customScalbInt2(long number, boolean isNegative, int exponent) {
        return number * powerOfTwo(isNegative, exponent);
    }
    /**
     * Bias used in the exponent of a float.
     */
    private static final int FLOAT_EXPONENT_BIAS = 127;
    /**
     * The number of bits in the significand, including the implicit bit.
     */
    private static final int FLOAT_SIGNIFICAND_WIDTH = 24;

    static float powerOfTwo(boolean isNegative, int exponent) {
        int floatSign = isNegative ? 1 << 31 : 0;
        int floatExponent = (exponent + FLOAT_EXPONENT_BIAS) << (FLOAT_SIGNIFICAND_WIDTH - 1);
        int floatValue = floatSign | floatExponent;
        return Float.intBitsToFloat(floatValue);
    }
}
