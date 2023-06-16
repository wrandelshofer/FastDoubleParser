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
 * (Max Turbo Frequency: 4.60 GHz = 0.217 ns)
 * </pre>
 * Benchmark                                  Mode  Cnt  Score   Error  Units
 * JmhScalb.mFastScalbDouble                  avgt    4  0.692 ± 0.072  ns/op
 * JmhScalb.mFastScalbDoubleFastNegative      avgt    4  0.868 ± 0.107  ns/op
 * JmhScalb.mFastScalbDoubleNegative          avgt    4  0.874 ± 0.117  ns/op
 * JmhScalb.mFastScalbFloat                   avgt    4  0.624 ± 0.073  ns/op
 * JmhScalb.mMathScalb1DoubleNegative         avgt    4  1.943 ± 0.291  ns/op
 * JmhScalb.mMathScalbDouble                  avgt    4  1.799 ± 0.085  ns/op
 * JmhScalb.mMathScalbDouble1                 avgt    4  1.632 ± 0.144  ns/op
 * JmhScalb.mMathScalbFloat                   avgt    4  1.942 ± 0.024  ns/op
 * JmhScalb.mMathScalbFloat1                  avgt    4  1.146 ± 0.141  ns/op
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
    public static final int DOUBLE_EXPONENT_BIAS = 1023;
    /**
     * The number of bits in the significand, including the implicit bit.
     */
    public static final int DOUBLE_SIGNIFICAND_WIDTH = 53;
    /**
     * Bias used in the exponent of a float.
     */
    private static final int FLOAT_EXPONENT_BIAS = 127;
    /**
     * The number of bits in the significand, including the implicit bit.
     */
    private static final int FLOAT_SIGNIFICAND_WIDTH = 24;
    double d;
    float f;
    boolean isNegative;
    long i;
    int scaleFactorD;
    int scaleFactorF;

    static double fastScalbDouble(double number, int scaleFactor) {
        return number * Double.longBitsToDouble((long) (scaleFactor + DOUBLE_EXPONENT_BIAS) << (DOUBLE_SIGNIFICAND_WIDTH - 1));
    }

    static double fastScalbDouble(boolean isNegative, double number, long exponent) {
        return number * powerOfTwo(isNegative, exponent);
    }

    static double powerOfTwo(boolean isNegative, long exponent) {
        long doubleSign = isNegative ? 1L << 63 : 0;
        long doubleExponent = (exponent + DOUBLE_EXPONENT_BIAS) << (DOUBLE_SIGNIFICAND_WIDTH - 1);
        long doubleValue = doubleSign | doubleExponent;
        return Double.longBitsToDouble(doubleValue);
    }

    static float fastScalbFloat(float number, int scaleFactor) {
        return number * Float.intBitsToFloat((scaleFactor + FLOAT_EXPONENT_BIAS) << (FLOAT_SIGNIFICAND_WIDTH - 1));
    }

    @Setup
    public void prepare() {
        Random rng = new Random();
        d = rng.nextDouble();
        isNegative = rng.nextBoolean();
        i = (long) (d * 120423423423L);
        f = (float) d;
        scaleFactorD = rng.nextInt(Double.MIN_EXPONENT, Double.MAX_EXPONENT);
        scaleFactorF = rng.nextInt(Float.MIN_EXPONENT, Float.MAX_EXPONENT);
    }

    @Benchmark
    public double mMathScalbDouble() {
        return Math.scalb(d, scaleFactorD);
    }

    @Benchmark
    public double mMathScalb1DoubleNegative() {
        double v = d * Math.scalb(1d, scaleFactorD);
        return isNegative ? -v : v;
    }

    @Benchmark
    public double mMathScalbDouble1() {
        return d * Math.scalb(1d, scaleFactorD);
    }

    @Benchmark
    public double mFastScalbDouble() {
        return fastScalbDouble(d, scaleFactorD);
    }

    @Benchmark
    public double mFastScalbDoubleNegative() {
        double v = fastScalbDouble(d, scaleFactorD);
        return isNegative ? -v : v;
    }

    @Benchmark
    public double mFastScalbDoubleFastNegative() {
        return fastScalbDouble(isNegative, d, scaleFactorD);
    }

    @Benchmark
    public float mMathScalbFloat() {
        return Math.scalb(f, scaleFactorF);
    }

    @Benchmark
    public float mMathScalbFloat1() {
        return f * Math.scalb(1f, scaleFactorF);
    }

    @Benchmark
    public float mFastScalbFloat() {
        return fastScalbFloat(f, scaleFactorF);
    }
}
