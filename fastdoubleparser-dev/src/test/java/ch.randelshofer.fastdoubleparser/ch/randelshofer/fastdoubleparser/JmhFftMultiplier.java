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
 *           (digits)   Mode  Cnt          _        Score   Error  Units
 * bigIntMul               1  avgt         _       12.542          ns/op
 * bigIntMul              10  avgt         _       61.165          ns/op
 * bigIntMul             100  avgt         _     2230.654          ns/op
 * bigIntMul            1000  avgt         _   125126.791          ns/op
 * bigIntMul           10000  avgt         _  4345217.581          ns/op
 * bigIntMul          100000  avgt         _130398426.351          ns/op
 * bigIntMul         1000000  avgt        3_793324687.333          ns/op
 * bigIntMul        10000000  avgt       95_997865178.000          ns/op
 * bigIntParaMul           1  avgt         _       12.728          ns/op
 * bigIntParaMul          10  avgt         _      104.104          ns/op
 * bigIntParaMul         100  avgt         _     2243.226          ns/op
 * bigIntParaMul        1000  avgt         _    94033.287          ns/op
 * bigIntParaMul       10000  avgt         _  1534321.503          ns/op
 * bigIntParaMul      100000  avgt         _ 38690763.788          ns/op
 * bigIntParaMul     1000000  avgt        1_078235959.000          ns/op
 * bigIntParaMul    10000000  avgt       30_959947020.000          ns/op
 * fftMul                  1  avgt    2    _      313.712          ns/op
 * fftMul                 10  avgt    2    _     1744.232          ns/op
 * fftMul                100  avgt    2    _    13853.314          ns/op
 * fftMul               1000  avgt    2    _   188580.924          ns/op
 * fftMul              10000  avgt    2    _  3362984.192          ns/op
 * fftMul             100000  avgt    2    _ 38295528.431          ns/op
 * fftMul            1000000  avgt    2   1_272489768.660          ns/op
 * fftMul           10000000  avgt    2  12_947464540.000          ns/op
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
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
            , "10000"
            , "100000"
            , "1000000"
            , "10000000"
            //  "100000000"
            // , "646391315"

    })
    public int digits;
    private BigInteger a;
    private BigInteger b;

    @Setup(Level.Trial)
    public void setUp() {
        byte[] bytesA = new byte[(int) FastIntegerMath.estimateNumBits(digits)];
        byte[] bytesB = new byte[(int) FastIntegerMath.estimateNumBits(digits)];
        Random rng = new Random(0);
        rng.nextBytes(bytesA);
        rng.nextBytes(bytesB);
        a = new BigInteger(bytesA);
        b = new BigInteger(bytesB);

    }

/*
    @Benchmark
    public BigInteger bigIntMul() {
        return a.multiply(b);
    }

    @Benchmark
    public BigInteger bigIntParaMul() {
        return a.parallelMultiply(b);
    }
*/

    @Benchmark
    public BigInteger fftMul() {
        return FftMultiplier.multiplyFFT(a, b);
    }
}





