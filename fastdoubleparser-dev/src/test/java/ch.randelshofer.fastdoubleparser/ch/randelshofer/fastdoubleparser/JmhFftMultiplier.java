/*
 * @(#)JmhFftMultiplier.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected integer strings.
 * <pre>
 * After optimising FftMultiplier.calculateRootsOfUnity().
 *
 * # JMH version: 1.35
 * # VM version: JDK 20, OpenJDK 64-Bit Server VM, 20+36-2344
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *            (digits)  Mode  Cnt           Score           Error  Units
 * bigIntMul         1  avgt    4          15.662 ±        15.089  ns/op
 * bigIntMul        10  avgt    4          18.789 ±         0.790  ns/op
 * bigIntMul       100  avgt    4          66.890 ±         2.157  ns/op
 * bigIntMul      1000  avgt    4        2879.404 ±       212.172  ns/op
 * bigIntMul     10000  avgt    4      143063.634 ±      2394.130  ns/op
 * bigIntMul    100000  avgt    4     4970366.090 ±    104875.966  ns/op
 * bigIntMul   1000000  avgt    4   149379252.884 ±   1914476.255  ns/op
 * bigIntMul  10000000  avgt    4  4750927556.917 ± 146419606.143  ns/op
 * fftMul            1  avgt    4         155.971 ±         0.764  ns/op
 * fftMul           10  avgt    4         262.854 ±        12.296  ns/op
 * fftMul          100  avgt    4        1333.530 ±        50.627  ns/op
 * fftMul         1000  avgt    4       11108.823 ±       239.470  ns/op
 * fftMul        10000  avgt    4      120515.334 ±      2228.828  ns/op
 * fftMul       100000  avgt    4     1814627.285 ±     42080.554  ns/op
 * fftMul      1000000  avgt    4    23459993.720 ±    633681.521  ns/op
 * fftMul     10000000  avgt    4   402455109.630 ±   6801078.584  ns/op
 * fftSquare         1  avgt    4         113.592 ±         1.179  ns/op
 * fftSquare        10  avgt    4         193.357 ±         3.009  ns/op
 * fftSquare       100  avgt    4         982.641 ±        21.776  ns/op
 * fftSquare      1000  avgt    4        7848.957 ±       273.394  ns/op
 * fftSquare     10000  avgt    4       89564.590 ±      4003.313  ns/op
 * fftSquare    100000  avgt    4     1369116.736 ±     71219.938  ns/op
 * fftSquare   1000000  avgt    4    18107402.389 ±   1516129.734  ns/op
 * fftSquare  10000000  avgt    4   305210971.932 ±   5031926.669  ns/op
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
        , "-Xmx24g"
        // , "--add-opens", "java.base/java.math=ALL-UNNAMED"

        // Options for analysis with https://github.com/AdoptOpenJDK/jitwatch
        //, "-XX:+UnlockDiagnosticVMOptions"
        //, "-Xlog:class+load=info"
        //, "-XX:+LogCompilation"
        //, "-XX:+PrintAssembly"

})
@Measurement(iterations = 4)
@Warmup(iterations = 4)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhFftMultiplier {


    @Param({
            "1"
            , "10"
            , "100"
            , "1000"
            // , "5000"
            // , "6000"
            // , "7000"
            // , "8000"
            // , "9000"
            , "10000"
            , "100000"
            , "1000000"
            , "10000000"
            , "100000000"
            , "323195659"
//
    })
    public int digits;
    private BigInteger a;
    private BigInteger b;
    private FftMultiplier.ComplexVector complexVector;

    @Setup(Level.Trial)
    public void setUp() {
        long estimatedNumBits = Math.min(1L << 29, FastIntegerMath.estimateNumBits(digits));
        //System.out.println("estimatedNumBits=" + estimatedNumBits);
        int estimatedNumBytes = (int) ((estimatedNumBits + 7) >> 3);
        byte[] bytesA = new byte[estimatedNumBytes];
        byte[] bytesB = new byte[estimatedNumBytes];
        Random rng = new Random(0);
        rng.nextBytes(bytesA);
        rng.nextBytes(bytesB);
        a = new BigInteger(1, bytesA);
        b = new BigInteger(1, bytesB);
        complexVector = FftMultiplier.toFftVector(a.toByteArray(), 3145728, 11);
    }


    @Benchmark
    public BigInteger bigIntMul() {
        return a.multiply(b);
    }

    @Benchmark
    public BigInteger fftMul() {
        return FftMultiplier.multiplyFft(a, b);
    }

    @Benchmark
    public BigInteger fftSquare() {
        return FftMultiplier.squareFft(a);
    }
}





