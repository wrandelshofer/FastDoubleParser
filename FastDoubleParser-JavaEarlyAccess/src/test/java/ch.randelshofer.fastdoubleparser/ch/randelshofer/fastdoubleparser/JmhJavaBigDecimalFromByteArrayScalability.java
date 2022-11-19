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
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+24-1795
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *     (digits)  Mode  Cnt            Score   Error  Units
 * fp          1  avgt    2           11.440          ns/op
 * fp         10  avgt    2           16.926          ns/op
 * fp        100  avgt    2          551.558          ns/op
 * fp       1000  avgt    2         6278.705          ns/op
 * fp      10000  avgt    2       198469.186          ns/op
 * fp     100000  avgt    2      3970291.505          ns/op
 * fp    1000000  avgt    2     94821977.307          ns/op
 * fp   10000000  avgt    2   2518137052.625          ns/op
 * fp  100000000  avgt    2  82159761598.000          ns/op
 *
 *     (digits)  Mode  Cnt             Score   Error  Units
 * f          1  avgt    2            15.866          ns/op
 * f         10  avgt    2            19.659          ns/op
 * f        100  avgt    2           699.238          ns/op
 * f       1000  avgt    2          7076.743          ns/op
 * f      10000  avgt    2        308201.856          ns/op
 * f     100000  avgt    2      10185539.382          ns/op
 * f    1000000  avgt    2     324529829.494          ns/op
 * f   10000000  avgt    2    7606351418.750          ns/op
 * f  100000000  avgt    2  220240847843.500          ns/op
 * </pre>
 *
 * <pre>
 * # JMH version: 1.35
 * # VM version: OpenJDK 64-Bit Server VM, Oracle Corporation, 20-ea+22-1594
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 * parse only:
 *      (digits)  Mode  Cnt          Score   Error  Units
 * fp           1  avgt    2         10.046          ns/op
 * fp          10  avgt    2         14.924          ns/op
 * fp         100  avgt    2         21.201          ns/op
 * fp        1000  avgt    2         75.387          ns/op
 * fp       10000  avgt    2        629.977          ns/op
 * fp      100000  avgt    2       7197.921          ns/op
 * fp     1000000  avgt    2      93134.505          ns/op
 * fp    10000000  avgt    2    1324385.898          ns/op
 * fp   100000000  avgt    2   13841659.306          ns/op
 * fp  1000000000  avgt    2  139549257.979          ns/op
 * fp  1292782621  avgt    2  183389538.331          ns/op
 * ip           1  avgt    2          7.095          ns/op
 * ip          10  avgt    2         12.980          ns/op
 * ip         100  avgt    2         18.822          ns/op
 * ip        1000  avgt    2         81.936          ns/op
 * ip       10000  avgt    2        712.674          ns/op
 * ip      100000  avgt    2       6844.904          ns/op
 * ip     1000000  avgt    2      86105.059          ns/op
 * ip    10000000  avgt    2    1241240.895          ns/op
 * ip   100000000  avgt    2   12981130.860          ns/op
 * ip  1000000000  avgt    2  133794721.567          ns/op
 * ip  1292782621  avgt    2  170464284.271          ns/op
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
public class JmhJavaBigDecimalFromByteArrayScalability {


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
            // "1000000000"
            //  "1292782621"
            //"646391314"
    })
    public int digits;
    private byte[] integerPart;
    private byte[] fractionalPart;

    @Setup(Level.Trial)
    public void setUp() {
        String str = "9806543217".repeat((digits + 9) / 10).substring(0, digits);
        integerPart = str.getBytes(StandardCharsets.ISO_8859_1);
        fractionalPart = ("1." + str).getBytes(StandardCharsets.ISO_8859_1);
    }


    //  @Benchmark
    //  public BigDecimal i() {
    //      return JavaBigDecimalParser.parseBigDecimal(integerPart);
    //  }
//
    @Benchmark
    public BigDecimal fp() {
        return JavaBigDecimalParser.parallelParseBigDecimal(fractionalPart);
    }

    @Benchmark
    public BigDecimal f() {
        return JavaBigDecimalParser.parseBigDecimal(fractionalPart);
    }


}





