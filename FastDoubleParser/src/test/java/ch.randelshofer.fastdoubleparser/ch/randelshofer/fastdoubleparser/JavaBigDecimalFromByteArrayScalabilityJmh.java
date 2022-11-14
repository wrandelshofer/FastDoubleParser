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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+22-1594
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *     (digits)  Mode  Cnt            Score   Error  Units
 * f     _   _100  avgt    2    _   _   _233.408          ns/op
 * f     _  1_000  avgt    2    _   _  5_738.753          ns/op
 * f     _ 10_000  avgt    2    _   _179_459.039          ns/op
 * f     _100_000  avgt    2    _  2_700_652.891          ns/op
 * f    1_000_000  avgt    2    _ 69_479_985.515          ns/op
 * f   10_000_000  avgt    2   1_954_418_855.167          ns/op
 * f  100_000_000  avgt    2  61_274_573_007.500          ns/op
 * </pre>
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 1.8.0_281, Java HotSpot(TM) 64-Bit Server VM, 25.281-b09
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Java 8 does not have method {@link BigInteger#parallelMultiply}.
 *
 *     (digits)  Mode  Cnt             Score   Error  Units
 * f     _   _ 10  avgt    2     _   _   _ 24.882          ns/op
 * f     _   _100  avgt    2     _   _   _379.437          ns/op
 * f     _  1_000  avgt    2     _   _  5_664.676          ns/op
 * f     _ 10_000  avgt    2     _   _156_205.293          ns/op
 * f     _100_000  avgt    2     _  4_670_936.428          ns/op
 * f    1_000_000  avgt    2     _141_024_651.174          ns/op
 * f   10_000_000  avgt    2    3_803_276_144.000          ns/op
 * f  100_000_000  avgt    2  118_674_774_068.000          ns/op
 * </pre>
 *
 * <pre>
 * # JMH version: 1.35
 * # VM version: OpenJDK 64-Bit Server VM, Oracle Corporation, 20-ea+22-1594
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 * parse only:
 *      (digits)  Mode  Cnt          Score   Error  Units
 * f           1  avgt    2         10.046          ns/op
 * f          10  avgt    2         14.924          ns/op
 * f         100  avgt    2         21.201          ns/op
 * f        1000  avgt    2         75.387          ns/op
 * f       10000  avgt    2        629.977          ns/op
 * f      100000  avgt    2       7197.921          ns/op
 * f     1000000  avgt    2      93134.505          ns/op
 * f    10000000  avgt    2    1324385.898          ns/op
 * f   100000000  avgt    2   13841659.306          ns/op
 * f  1000000000  avgt    2  139549257.979          ns/op
 * f  1292782621  avgt    2  183389538.331          ns/op
 * i           1  avgt    2          7.095          ns/op
 * i          10  avgt    2         12.980          ns/op
 * i         100  avgt    2         18.822          ns/op
 * i        1000  avgt    2         81.936          ns/op
 * i       10000  avgt    2        712.674          ns/op
 * i      100000  avgt    2       6844.904          ns/op
 * i     1000000  avgt    2      86105.059          ns/op
 * i    10000000  avgt    2    1241240.895          ns/op
 * i   100000000  avgt    2   12981130.860          ns/op
 * i  1000000000  avgt    2  133794721.567          ns/op
 * i  1292782621  avgt    2  170464284.271          ns/op
 * </pre>
 *
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
public class JavaBigDecimalFromByteArrayScalabilityJmh {


    @Param({
            //"1"
            //, "10"
            "100"
            , "1000"
            , "10000"
            , "100000"
            , "1000000"
            , "10000000"
            , "100000000"
            //  ,"1000000000"
            //  "1292782621"
    })
    public int digits;
    private byte[] integerPart;
    private byte[] fractionalPart;

    @Setup(Level.Trial)
    public void setUp() {
        String str = "9806543217".repeat((digits + 9) / 10).substring(0, digits);
        integerPart = str.getBytes(StandardCharsets.ISO_8859_1);
        fractionalPart = ("." + str).getBytes(StandardCharsets.ISO_8859_1);
    }


    // @Benchmark
    // public BigDecimal i() {
    //     return JavaBigDecimalParser.parseBigDecimal(integerPart);
    // }

    @Benchmark
    public BigDecimal f() {
        return JavaBigDecimalParser.parseBigDecimal(fractionalPart);
    }


}





