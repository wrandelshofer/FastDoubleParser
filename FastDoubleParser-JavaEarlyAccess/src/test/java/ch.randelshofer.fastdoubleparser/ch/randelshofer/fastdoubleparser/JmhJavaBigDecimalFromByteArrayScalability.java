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
 *      (digits)  Mode  Cnt             Score   Error  Units
 * f           1  avgt    2            14.505          ns/op
 * f          10  avgt    2            19.631          ns/op
 * f         100  avgt    2           599.830          ns/op
 * f        1000  avgt    2          6864.292          ns/op
 * f       10000  avgt    2        291812.634          ns/op
 * f      100000  avgt    2       9894570.057          ns/op
 * f     1000000  avgt    2     314264045.875          ns/op
 * f    10000000  avgt    2    9647730488.750          ns/op
 * f   100000000  avgt    2  219840186170.000          ns/op
 * fp          1  avgt    2            11.359          ns/op
 * fp         10  avgt    2            16.575          ns/op
 * fp        100  avgt    2           570.128          ns/op
 * fp       1000  avgt    2          6877.465          ns/op
 * fp      10000  avgt    2        240066.359          ns/op
 * fp     100000  avgt    2       4884974.972          ns/op
 * fp    1000000  avgt    2      80530354.594          ns/op
 * fp   10000000  avgt    2    2335475538.100          ns/op
 * fp  100000000  avgt    2   72359890033.500          ns/op
 * ip          1  avgt    2             8.193          ns/op
 * ip         10  avgt    2            14.488          ns/op
 * ip        100  avgt    2           205.538          ns/op
 * ip       1000  avgt    2          5098.756          ns/op
 * ip      10000  avgt    2        123802.196          ns/op
 * ip     100000  avgt    2       2578333.298          ns/op
 * ip    1000000  avgt    2      69012871.814          ns/op
 * ip   10000000  avgt    2    1954652082.500          ns/op
 * ip  100000000  avgt    2   61191469918.500          ns/op
 * </pre>
 * <pre>
 * recursive only (recursion threshold=0)
 *    (digits)  Mode  Cnt           Score   Error  Units
 * ir         1  avgt    2          11.857          ns/op
 * ir        10  avgt    2          15.118          ns/op
 * ir       100  avgt    2         545.510          ns/op
 * ir      1000  avgt    2        6251.473          ns/op
 * ir     10000  avgt    2      196700.757          ns/op
 * ir    100000  avgt    2     6627368.179          ns/op
 * ir   1000000  avgt    2   209404227.114          ns/op
 * ir  10000000  avgt    2  6137410866.000          ns/op
 *
 * iterative only (recursion threshold=Integer.MAX_VALUE)
 *    (digits)  Mode  Cnt             Score   Error  Units
 * ii         1  avgt    2            12.875          ns/op
 * ii        10  avgt    2            18.067          ns/op
 * ii       100  avgt    2           218.580          ns/op
 * ii      1000  avgt    2          6205.464          ns/op
 * ii     10000  avgt    2        560241.743          ns/op
 * ii    100000  avgt    2      61370640.207          ns/op
 * ii   1000000  avgt    2    5376094842.000          ns/op
 * ii  10000000  avgt    2  507520375437.000          ns/op
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
            // , "100000000"
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

    /*
      @Benchmark
      public BigDecimal ip() {
          return JavaBigDecimalParser.parallelParseBigDecimal(integerPart);
      }
      */
    @Benchmark
    public BigDecimal i() {
        return JavaBigDecimalParser.parseBigDecimal(integerPart);
    }
  /*

    @Benchmark
    public BigDecimal fp() {
        return JavaBigDecimalParser.parallelParseBigDecimal(fractionalPart);
    }

    @Benchmark
    public BigDecimal f() {
        return JavaBigDecimalParser.parseBigDecimal(fractionalPart);
    }
*/
}





