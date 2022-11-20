/*
 * @(#)BigDecimalParserJmhBenchmark.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
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
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected integer strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+24-1795
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * With additional iterative step, recursion threshold 128
 *
 *      (digits)  Mode  Cnt     Score   Error  Units
 * dec       100  avgt    2   332.517          ns/op
 * dec      1000  avgt    2  4724.631          ns/op
 * </pre>
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+22-1594
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *        (digits)  Mode  Cnt            Score   Error  Units
 * hex    _   _  1  avgt    2       _   _ 24.032          ns/op
 * hex    _   _ 10  avgt    2       _   _ 36.407          ns/op
 * hex    _   _100  avgt    2       _   _145.416          ns/op
 * hex    _  1_000  avgt    2       _  1_167.143          ns/op
 * hex    _ 10_000  avgt    2       _ 11_504.050          ns/op
 * hex    _100_000  avgt    2       _109_226.495          ns/op
 * hex   1_000_000  avgt    2      1_103_771.706          ns/op
 * hex  10_000_000  avgt    2     13_125_587.576          ns/op * 1.3
 * hex 100_000_000  avgt    2    132_543_755.934          ns/op * 1.3
 *
 * decMaxValue 646391315 avgt 16 106_200_639_128.813 ± 1668854933.018  ns/op
 *
 * dec     _   _  1  avgt    2      _   _   _  8.358          ns/op
 * dec     _   _ 10  avgt    2      _   _   _ 18.853          ns/op
 * dec     _   _100  avgt    2      _   _   _511.649          ns/op
 * dec     _  1_000  avgt    2      _   _  6_145.889          ns/op
 * dec     _ 10_000  avgt    2      _   _129_424.889          ns/op
 * dec     _100_000  avgt    2      _  2_699_360.743          ns/op
 * dec    1_000_000  avgt    2      _ 75_137_833.724          ns/op
 * dec   10_000_000  avgt    2     2_111_107_223.800          ns/op
 * dec  100_000_000  avgt    2    65_650_510_183.500          ns/op
 * dec  646_391_315  avgt    2  1_127_781_166_163.500         ns/op
 *
 * recursive only:
 *      (digits)  Mode  Cnt                Score   Error  Units
 * dec    _   _100  avgt    2        _   _   _505.243          ns/op
 * dec    _  1_000  avgt    2        _   _  5_914.067          ns/op
 * dec    _ 10_000  avgt    2        _   _181_069.960          ns/op * 18
 * dec    _100_000  avgt    2        _  6_601_322.702          ns/op * 66
 * dec   1_000_000  avgt    2        _210_673_127.948          ns/op * 210
 * dec  10_000_000  avgt    2       6_317_343_012.000          ns/op * 631
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"

        //       ,"-XX:+UnlockDiagnosticVMOptions", "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/JavaBigDecimalParser.*"

})
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhJavaBigIntegerFromByteArrayScalability {


    @Param({
            //"1"
            //, "10"
            "100"
            , "1000"
            //  , "10000"
            //  , "100000"
            //  , "1000000"
            //  , "10000000"
            //  , "100000000"
            //"646391315"

    })
    public int digits;
    private byte[] hexLiteral;
    private byte[] decLiteral;



    @Setup(Level.Trial)
    public void setUp() {
        String str = "9806543217".repeat((digits + 9) / 10).substring(0, digits);
        decLiteral = str.getBytes(StandardCharsets.ISO_8859_1);
        hexLiteral = ("0x" + str).getBytes(StandardCharsets.ISO_8859_1);
    }

    /*
        @Benchmark
        public BigInteger hex() {
            return JavaBigIntegerParser.parseBigInteger(hexLiteral);
        }
*/
    @Benchmark
    public BigInteger dec() {
        return JavaBigIntegerParser.parseBigInteger(decLiteral);
    }
}





