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
            "700"
            , "800" // above this value, variant with bit shift leads
            , "900"
            , "2000"
            , "10000"
            , "30000"
    })
    public int bits;
    private BigInteger a;
    private BigInteger b;
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
        System.out.println(b.getLowestSetBit() + " bits from " + length * 8 + " in total are zero");
    }


    @Benchmark
    public void original(Blackhole blackhole) {
        blackhole.consume(FftMultiplier.multiply(a, b));
    }

    @Benchmark
    public void optimized(Blackhole blackhole) {
        blackhole.consume(multiply(a, b, zeroes * 8));
    }

    // early identical copy of existing method FftMultiplier.multiply() to imitate real performance gain
    static BigInteger multiply(BigInteger a, BigInteger b, int shift) {
        if (b.signum() == 0 || a.signum() == 0) {
            return BigInteger.ZERO;
        }

        int xlen = a.bitLength();
        int ylen = b.bitLength();
        if ((long) xlen + ylen > 32L * MAX_MAG_LENGTH) {
            throw new ArithmeticException("BigInteger would overflow supported range");
        }

        if (xlen > TOOM_COOK_THRESHOLD
                && ylen > TOOM_COOK_THRESHOLD
                && (xlen > FFT_THRESHOLD || ylen > FFT_THRESHOLD)) {
            throw new IllegalStateException("should not be tested here");
        }
        return a.multiply(b.shiftRight(shift)).shiftLeft(shift);
    }

    private static final int FFT_THRESHOLD = 33220;
    private static final int MAX_MAG_LENGTH = Integer.MAX_VALUE / Integer.SIZE + 1; // (1 << 26)
    private static final int TOOM_COOK_THRESHOLD = 240 * 8;
}





