/*
 * @(#)JmhJavaBigIntegerFromByteArrayScalability.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
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
 * # JMH version: 1.35
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+29-2280
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *                 (digits)  Mode  Cnt             Score   Error  Units
 * bigIntMul              1  avgt          _        0.744          ns/op
 * bigIntMul             10  avgt          _       10.175          ns/op
 * bigIntMul            100  avgt          _       69.120          ns/op
 * bigIntMul           1000  avgt          _     2751.164          ns/op
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
 * fftMul                 1  avgt          _       94.338          ns/op
 * fftMul                10  avgt          _      278.424          ns/op
 * fftMul               100  avgt          _     1649.438          ns/op
 * fftMul              1000  avgt          _    13270.498          ns/op
 * fftMul             10000  avgt          _   174858.465          ns/op
 * fftMul            100000  avgt          _  2628755.454          ns/op
 * fftMul           1000000  avgt          _ 44124072.753          ns/op
 * fftMul          10000000  avgt          _925856745.091          ns/op
 * fftMul         100000000  avgt        10_995622424.000          ns/op
 * mul                    1  avgt          _        0.658          ns/op
 * mul                   10  avgt          _       10.517          ns/op
 * mul                  100  avgt          _       67.746          ns/op
 * mul                 1000  avgt          _     2685.683          ns/op
 * mul                10000  avgt          _   173804.003          ns/op
 * mul               100000  avgt          _  2621671.866          ns/op
 * mul              1000000  avgt          _ 43915961.693          ns/op
 * mul             10000000  avgt          _897308742.583          ns/op
 * mul            100000000  avgt        10_951994069.000          ns/op
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
@Measurement(iterations = 1)
@Warmup(iterations = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhFftMultiplier {


    @Param({
            // "1"
            // , "10"
            // , "100"
            // , "1000"
            // , "10000"
            // , "100000"
            // , "1000000"
            // , "10000000"
            // , "100000000"
            "323195659"

    })
    public int digits;
    private BigInteger a;
    private BigInteger b;

    @Setup(Level.Trial)
    public void setUp() {
        long estimatedNumBits = FastIntegerMath.estimateNumBits(digits);
        int estimatedNumBytes = (int) (estimatedNumBits >> 3);
        byte[] bytesA = new byte[estimatedNumBytes];
        byte[] bytesB = new byte[estimatedNumBytes];
        Random rng = new Random(0);
        rng.nextBytes(bytesA);
        rng.nextBytes(bytesB);
        a = new BigInteger(1, bytesA);
        b = new BigInteger(1, bytesB);

    }

    @Benchmark
    public BigInteger bigIntMul() {
        return a.multiply(b);
    }

    @Benchmark
    public BigInteger bigIntParaMul() {
        return a.parallelMultiply(b);
    }

    @Benchmark
    public BigInteger fftMul() {
        return FftMultiplier.multiplyFft(a, b);
    }

    @Benchmark
    public BigInteger mul() {
        return FftMultiplier.multiply(a, b, false);
    }

}





