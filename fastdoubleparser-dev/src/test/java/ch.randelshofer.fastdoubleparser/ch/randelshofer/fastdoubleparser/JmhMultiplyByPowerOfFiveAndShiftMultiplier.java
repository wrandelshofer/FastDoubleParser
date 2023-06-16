/*
 * @(#)JmhMultiplyByPowerOfFiveAndShiftMultiplier.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.36
 * # VM version: JDK 20.0.1, OpenJDK 64-Bit Server VM, 20.0.1+9-29
 * # Intel(R) Core(TM) i5-6200U CPU @ 2.30GHz
 *
 * Benchmark  (bits)  Mode  Cnt       Score      Error  Units
 * optimized     700  avgt    4     250.376 ±    3.773  ns/op
 * optimized     800  avgt    4     317.921 ±   30.518  ns/op
 * optimized     900  avgt    4     405.584 ±    2.953  ns/op
 * optimized    2000  avgt    4    1371.457 ±   15.372  ns/op
 * optimized   10000  avgt    4   30015.376 ± 1073.798  ns/op
 * optimized   30000  avgt    4  180735.583 ±  825.962  ns/op
 * original      700  avgt    4     254.843 ±    1.414  ns/op
 * original      800  avgt    4     339.564 ±    2.898  ns/op
 * original      900  avgt    4     463.033 ±    4.538  ns/op
 * original     2000  avgt    4    1828.848 ±  121.480  ns/op
 * original    10000  avgt    4   33663.669 ± 3189.014  ns/op
 * original    30000  avgt    4  204170.758 ± 3690.179  ns/op
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
})
@Measurement(iterations = 4, time = 1)
@Warmup(iterations = 4, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhMultiplyByPowerOfFiveAndShiftMultiplier {


    @Param({
            "700" // above this value, variant with bit shift leads
            , "800"
            , "900"
            , "2000"
            , "10000"
            , "30000"
    })
    public int bits;
    private BigInteger a;
    private BigInteger b;
    private BigInteger bs;
    public int zeroes;

    @Setup(Level.Trial)
    public void setUp() {
        int length = (bits + 7) / 8;
        byte[] bytesA = new byte[length];
        byte[] bytesB = new byte[length];
        Random rng = new Random(0);
        rng.nextBytes(bytesA);
        rng.nextBytes(bytesB);

        // to be positive
        bytesA[0] &= ~(1L << 63);
        bytesB[0] &= ~(1L << 63);

        // set for b rightmost zeroes like 10^n has
        final double lg5 = Math.log(5) / Math.log(2);
        zeroes = (int) (length / (lg5 + 1));
        for (int i = 0; i < zeroes; i++) {
            bytesB[length - 1 - i] = 0;
        }

        a = new BigInteger(1, bytesA);
        b = new BigInteger(1, bytesB);
        bs = b.shiftRight(zeroes * 8); // preshift value - imitate 5^n
        System.out.println(b.getLowestSetBit() + " bits from " + length * 8 + " in total are zero");
    }


    @Benchmark
    public void original(Blackhole blackhole) {
        blackhole.consume(FftMultiplier.multiply(a, b));
    }

    @Benchmark
    public void optimized(Blackhole blackhole) {
        blackhole.consume(FftMultiplier.multiply(a, bs).shiftLeft(zeroes * 8));
    }
}





