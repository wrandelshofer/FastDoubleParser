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
 * Benchmark       (digits)  Mode  Cnt     _        Score   Error  Units
 * fftMul                 1  avgt    2     _      158.802          ns/op
 * fftMul                10  avgt    2     _      273.737          ns/op
 * fftMul               100  avgt    2     _     1380.586          ns/op
 * fftMul              1000  avgt    2     _    11375.487          ns/op
 * fftMul              5000  avgt    2     _    61001.775          ns/op
 * fftMul              6000  avgt    2     _    88805.441          ns/op
 * fftMul              7000  avgt    2     _    92513.482          ns/op
 * fftMul              8000  avgt    2     _   115086.615          ns/op
 * fftMul              9000  avgt    2     _   119289.776          ns/op
 * fftMul             10000  avgt    2     _   121724.031          ns/op
 * fftMul            100000  avgt    2     _  1814285.567          ns/op
 * fftMul           1000000  avgt    2     _ 26303655.749          ns/op
 * fftMul          10000000  avgt    2     _409355950.960          ns/op
 * fftMul         100000000  avgt    2    4_894774550.500          ns/op
 * fftMul         323195659  avgt    2    9_322687438.500          ns/op
 * </pre>
 * <pre>
 * Before optimising FftMultiplier.calculateRootsOfUnity().
 *
 * # JMH version: 1.35
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+29-2280
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *                 (digits)  Mode  Cnt             Score   Error  Units
 * bigIntMul              1  avgt          _        0.744          ns/op
 * bigIntMul             10  avgt          _       10.175          ns/op
 * bigIntMul            100  avgt          _       69.120          ns/op
 * bigIntMul           1000  avgt          _     2751.164          ns/op
 * bigIntMul           5000  avgt               49006.859          ns/op
 * bigIntMul           6000  avgt               62359.419          ns/op
 * bigIntMul           7000  avgt               87777.763          ns/op
 * bigIntMul           8000  avgt              108487.443          ns/op
 * bigIntMul           9000  avgt              122055.372          ns/op
 * bigIntMul          10000  avgt              139125.192          ns/op
 * bigIntMul          10000  avgt          _   139652.212          ns/op
 * bigIntMul         100000  avgt          _  4892998.360          ns/op
 * bigIntMul        1000000  avgt          _146510318.377          ns/op
 * bigIntMul       10000000  avgt         4_544668420.000          ns/op
 * bigIntMul      100000000  avgt       136_610227832.000          ns/op
 * bigIntMul      323195659  avgt       768_288129903.000          ns/op
 * bigIntParaMul          1  avgt          _        0.718          ns/op
 * bigIntParaMul         10  avgt          _        9.866          ns/op
 * bigIntParaMul        100  avgt          _       66.354          ns/op
 * bigIntParaMul       1000  avgt          _     2680.353          ns/op
 * bigIntParaMul      10000  avgt          _    91212.377          ns/op
 * bigIntParaMul     100000  avgt          _  1835446.097          ns/op
 * bigIntParaMul    1000000  avgt          _ 45372758.959          ns/op
 * bigIntParaMul   10000000  avgt         1_572644848.286          ns/op
 * bigIntParaMul  100000000  avgt        51_212775272.000          ns/op
 * bigIntParaMul  323195659  avgt       297_916512893.000          ns/op
 * fftMul                 1  avgt          _      163.768          ns/op
 * fftMul                10  avgt          _      275.918          ns/op
 * fftMul               100  avgt          _     1437.130          ns/op
 * fftMul              1000  avgt          _    12068.933          ns/op
 * fftMul              5000  avgt               65503.577          ns/op
 * fftMul              6000  avgt              102141.575          ns/op
 * fftMul              7000  avgt               98315.762          ns/op
 * fftMul              8000  avgt              122717.239          ns/op
 * fftMul              9000  avgt              125783.198          ns/op
 * fftMul             10000  avgt          _   129202.977          ns/op
 * fftMul            100000  avgt          _  1948854.827          ns/op
 * fftMul           1000000  avgt          _ 28343218.317          ns/op
 * fftMul          10000000  avgt          _449109977.913          ns/op
 * fftMul         100000000  avgt         5_116104944.500          ns/op
 * fftMul         323195659  avgt        13_690917821.000          ns/op
 * fftSquare              1  avgt    2     _      128.627          ns/op
 * fftSquare             10  avgt    2     _      214.154          ns/op
 * fftSquare            100  avgt    2     _     1155.262          ns/op
 * fftSquare           1000  avgt    2     _     9824.025          ns/op
 * fftSquare          10000  avgt    2     _   107915.890          ns/op
 * fftSquare         100000  avgt    2     _  1755018.665          ns/op
 * fftSquare        1000000  avgt    2     _ 33168012.215          ns/op
 * fftSquare       10000000  avgt    2     _468049376.273          ns/op
 * fftSquare      100000000  avgt    2    3_770272005.333          ns/op
 * fftSquare      323195659  avgt    2    9_537786000.250          ns/op
 * mul                    1  avgt          _       13.713          ns/op
 * mul                   10  avgt          _       19.097          ns/op
 * mul                  100  avgt          _       65.922          ns/op
 * mul                 1000  avgt          _     2753.316          ns/op
 * mul                 5000  avgt          _    46420.694          ns/op
 * mul                 6000  avgt          _    59641.915          ns/op
 * mul                 7000  avgt          _    84799.189          ns/op
 * mul                 8000  avgt          _    99404.765          ns/op
 * mul                 9000  avgt          _   119522.130          ns/op
 * mul                10000  avgt          _   127678.434          ns/op
 * mul               100000  avgt          _  1964966.407          ns/op
 * mul              1000000  avgt          _ 29734690.534          ns/op
 * mul             10000000  avgt          _445866861.913          ns/op
 * mul            100000000  avgt         5_164456660.500          ns/op
 * mul            323195659  avgt        13_102521069.000          ns/op
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
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhFftMultiplier {


    @Param({
            "1"
            , "10"
            , "100"
            , "1000"
            , "5000"
            , "6000"
            , "7000"
            , "8000"
            , "9000"
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
    }


    /*
    @Benchmark
    public BigInteger bigIntMul() {
        return a.multiply(b);
    }

*/
    @Benchmark
    public BigInteger fftMul() {
        return FftMultiplier.multiplyFft(a, b);
    }
    /*
    @Benchmark
    public BigInteger fftMulInterleaved() {
        return FftMultiplier.interleavedMultiplyFft(a, b);
    }

    @Benchmark
    public BigInteger fftSquare() {
        return FftMultiplier.squareFft(a);
    }


    @Benchmark
    public BigInteger mul() {
        return FftMultiplier.multiply(a, b, false);
    }
*/

}





