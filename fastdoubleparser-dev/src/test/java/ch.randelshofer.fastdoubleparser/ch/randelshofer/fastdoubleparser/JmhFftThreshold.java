/*
 * @(#)JmhMultiplyByFftVsByBigInteger.java
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
 * Benchmark   (bits)  Mode  Cnt    Score    Error  Units
 * bigInteger   44000  avgt    4  348.081 ±  8.025  us/op
 * bigInteger   44500  avgt    4  350.277 ± 35.341  us/op
 * bigInteger   45000  avgt    4  356.652 ± 24.410  us/op
 * fft          44000  avgt    4  350.903 ±  5.170  us/op
 * fft          44500  avgt    4  344.830 ±  2.741  us/op
 * fft          45000  avgt    4  343.596 ±  3.054  us/op
 *
 * Process finished with exit code 0
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
})
@Measurement(iterations = 4, time = 1)
@Warmup(iterations = 10, time = 1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhFftThreshold {

    @Param({"44000",
            "44500", // where both variants are equal - FftMultiplier.FFT_THRESHOLD
            "45000"})
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
    public void fft(Blackhole blackhole) {
        blackhole.consume(FftMultiplier.multiplyFft(a, b));
    }

    @Benchmark
    public void bigInteger(Blackhole blackhole) {
        blackhole.consume(a.multiply(bs).shiftLeft(zeroes * 8));
    }
}





