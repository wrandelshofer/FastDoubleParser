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
 * # VM version: OpenJDK 64-Bit Server VM, Oracle Corporation, 20-ea+22-1594
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * parallel threshold 1024, recursion threshold 128
 *     (digits)  Mode  Cnt             Score   Error  Units
 * f          1  avgt    2            13.596          ns/op
 * f         10  avgt    2            20.311          ns/op
 * f        100  avgt    2           219.335          ns/op
 * f       1000  avgt    2          5785.488          ns/op
 * f      10000  avgt    2        182105.412          ns/op
 * f     100000  avgt    2       4737761.989          ns/op
 * f    1000000  avgt    2     119902014.787          ns/op
 * f   10000000  avgt    2    3020700620.625          ns/op
 * f  100000000  avgt    2  108569300854.000          ns/op
 * i          1  avgt    2            12.968          ns/op
 * i         10  avgt    2            17.797          ns/op
 * i        100  avgt    2           226.355          ns/op
 * i       1000  avgt    2          5549.707          ns/op
 * i      10000  avgt    2        182570.960          ns/op
 * i     100000  avgt    2       4831606.773          ns/op
 * i    1000000  avgt    2     119433669.881          ns/op
 * i   10000000  avgt    2    3675417445.833          ns/op
 * i  100000000  avgt    2   95698142681.500          ns/op
 *
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
            "1"
            , "10"
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


    @Benchmark
    public BigDecimal i() {
        return JavaBigDecimalParser.parseBigDecimal(integerPart);
    }

    @Benchmark
    public BigDecimal f() {
        return JavaBigDecimalParser.parseBigDecimal(fractionalPart);
    }


}





