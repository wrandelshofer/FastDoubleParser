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
 * ssMul                  10  avgt         _    22966.537          ns/op
 * ssMul                 100  avgt         _   139383.867          ns/op
 * ssMul                1000  avgt         _   616674.326          ns/op
 * ssMul               10000  avgt         _ 18059424.838          ns/op
 * ssMul              100000  avgt         _175439610.431          ns/op
 * ssMul             1000000  avgt        1_986820296.667          ns/op
 * ssMul            10000000  avgt       18_989100691.000          ns/op
 * ssParaMul              10  avgt         _    20901.942          ns/op
 * ssParaMul             100  avgt         _   126681.468          ns/op
 * ssParaMul            1000  avgt         _   594489.145          ns/op
 * ssParaMul           10000  avgt         _ 18032670.980          ns/op
 * ssParaMul          100000  avgt         _183667194.073          ns/op
 * ssParaMul         1000000  avgt        2_003763367.667          ns/op
 * ssParaMul        10000000  avgt       19_285212245.000          ns/op
 *
 *
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
@Measurement(iterations = 1)
@Warmup(iterations = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhSchoenhageStrassenMultiplier {


    @Param({
            //"1"
            //, "10"
            //, "100"
            //, "1000"
            //, "10000"
            //,"100000"
            //"1000000"
            "10000000"
            // , "100000000"
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
    public BigInteger ssMul() {
        return SchoenhageStrassenMultiplier.multiplySchoenhageStrassen(a, b, false);
    }
    /*
    @Benchmark
    public BigInteger ssParaMul() {
        return SchoenhageStrassenMultiplier.multiplySchoenhageStrassen(a,b,true);
    }
*/
}





