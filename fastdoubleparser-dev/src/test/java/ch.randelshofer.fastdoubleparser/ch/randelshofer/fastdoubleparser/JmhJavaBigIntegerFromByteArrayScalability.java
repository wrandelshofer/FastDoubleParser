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
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static ch.randelshofer.fastdoubleparser.Strings.repeat;

/**
 * Benchmarks for selected integer strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+29-2280
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *        (digits)  Mode  Cnt              Score   Error  Units
 * pdec          1  avgt    2              3.296          ns/op
 * pdec         10  avgt    2             16.703          ns/op
 * pdec        100  avgt    2            492.084          ns/op
 * pdec       1000  avgt    2           5712.078          ns/op
 * pdec      10000  avgt    2         133442.586          ns/op
 * pdec     100000  avgt    2        2384745.139          ns/op
 * pdec    1000000  avgt    2       65651919.701          ns/op
 * pdec   10000000  avgt    2     1888062413.917          ns/op
 * pdec  100000000  avgt    2    58431764503.000          ns/op
 * pdec  646391315  avgt    2  1119163446185.000          ns/op
 * sdec          1  avgt    2              3.191          ns/op
 * sdec         10  avgt    2             13.364          ns/op
 * sdec        100  avgt    2            430.248          ns/op
 * sdec       1000  avgt    2           4929.324          ns/op
 * sdec      10000  avgt    2         160675.651          ns/op
 * sdec     100000  avgt    2        6229097.178          ns/op
 * sdec    1000000  avgt    2      202358769.620          ns/op
 * sdec   10000000  avgt    2     6000028631.500          ns/op
 * sdec  100000000  avgt    2   178840933781.500          ns/op
 * sdec  646391315  avgt    2  3014105673393.000          ns/op
 * </pre>
 *
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
 * hex         1  avgt    2           24.032          ns/op
 * hex        10  avgt    2           36.407          ns/op
 * hex       100  avgt    2          145.416          ns/op
 * hex      1000  avgt    2         1167.143          ns/op
 * hex     10000  avgt    2        11504.050          ns/op
 * hex    100000  avgt    2       109226.495          ns/op
 * hex   1000000  avgt    2      1103771.706          ns/op
 * hex  10000000  avgt    2     13125587.576          ns/op * 1.3
 * hex 100000000  avgt    2    132543755.934          ns/op * 1.3
 *
 * recursive only:
 *      (digits)  Mode  Cnt                Score   Error  Units
 * dec       100  avgt    2              505.243          ns/op
 * dec      1000  avgt    2             5914.067          ns/op
 * dec     10000  avgt    2           181069.960          ns/op * 18
 * dec    100000  avgt    2          6601322.702          ns/op * 66
 * dec   1000000  avgt    2        210673127.948          ns/op * 210
 * dec  10000000  avgt    2       6317343012.000          ns/op * 631
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
            "1"
            , "10"
            , "100"
            , "1000"
            , "10000"
            , "100000"
            , "1000000"
            , "10000000"
            , "100000000"
            , "646391315"

    })
    public int digits;
    private byte[] decLiteral;

    @Setup(Level.Trial)
    public void setUp() {
        String str = repeat("9806543217", (digits + 9) / 10).substring(0, digits);
        decLiteral = str.getBytes(StandardCharsets.ISO_8859_1);
    }


    @Benchmark
    public BigInteger hex() {
        return JavaBigIntegerParser.parseBigInteger(decLiteral, 16);
    }

    @Benchmark
    public BigInteger sdec() {
        return JavaBigIntegerParser.parseBigInteger(decLiteral);
    }

    @Benchmark
    public BigInteger pdec() {
        return JavaBigIntegerParser.parallelParseBigInteger(decLiteral);
    }
}





