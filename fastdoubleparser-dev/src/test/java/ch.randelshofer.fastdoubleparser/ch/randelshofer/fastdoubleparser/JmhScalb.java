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
 * Benchmark                    Mode  Cnt  Score   Error  Units
 * JmhScalb.mCustomScalbDouble  avgt    4  0.694 ± 0.124  ns/op
 * JmhScalb.mCustomScalbFloat   avgt    4  0.632 ± 0.080  ns/op
 * JmhScalb.mMathScalbDouble    avgt    4  1.859 ± 0.268  ns/op
 * JmhScalb.mMathScalbDouble1   avgt    4  1.647 ± 0.172  ns/op
 * JmhScalb.mMathScalbFloat     avgt    4  1.968 ± 0.072  ns/op
 * JmhScalb.mMathScalbFloat1    avgt    4  1.170 ± 0.103  ns/op
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
    long i;
    int scaleFactorD;
    int scaleFactorF;

    static double customScalbDouble(double number, int scaleFactor) {
        return number * Double.longBitsToDouble((long) (scaleFactor + DOUBLE_EXPONENT_BIAS) << (DOUBLE_SIGNIFICAND_WIDTH - 1));
    }

    static float customScalbFloat(float number, int scaleFactor) {
        return number * Float.intBitsToFloat((scaleFactor + FLOAT_EXPONENT_BIAS) << (FLOAT_SIGNIFICAND_WIDTH - 1));
    }

    @Setup
    public void prepare() {
        Random rng = new Random();
        d = rng.nextDouble();
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
    public double mMathScalbDouble1() {
        return d * Math.scalb(1d, scaleFactorD);
    }

    @Benchmark
    public double mCustomScalbDouble() {
        return customScalbDouble(d, scaleFactorD);
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
    public float mCustomScalbFloat() {
        return customScalbFloat(f, scaleFactorF);
    }
}
