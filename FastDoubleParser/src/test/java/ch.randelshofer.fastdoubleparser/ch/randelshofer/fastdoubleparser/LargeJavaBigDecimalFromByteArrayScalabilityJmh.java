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
 * parallel threshold 1,024 digits, recursion threshold 256 digits:
 *    (digits)  Mode  Cnt                  Score   Error  Units
 * i        20  avgt    2                 60.728          ns/op
 * i        30  avgt    2                 73.117          ns/op
 * i        40  avgt    2                 80.207          ns/op
 * i        50  avgt    2                 99.299          ns/op
 * i        60  avgt    2                119.645          ns/op
 * i        70  avgt    2                140.226          ns/op
 * i        80  avgt    2                150.224          ns/op
 * i        90  avgt    2                174.160          ns/op
 * i       100  avgt    2                200.758          ns/op
 * i      1000  avgt    2           ,  4,809.725          ns/op
 * i     10000  avgt    2           ,148,277.995          ns/op
 * i    100000  avgt    2          4,220,246.269          ns/op
 * i   1000000  avgt    2        145,948,400.953          ns/op
 * parallel threshold 1,024 digits, recursion threshold 0 digits:
 *     (digits)  Mode  Cnt  ,   ,   ,   ,  Score             Error  Units Factor
 * f          1  avgt    5  ,   ,   ,   , 19.941 ±           6.364  ns/op
 * f         10  avgt    5  ,   ,   ,   , 29.859 ±           0.169  ns/op
 * f        100  avgt    5  ,   ,   ,   ,442.749 ±           3.695  ns/op
 * f       1000  avgt    5  ,   ,   ,  6,147.738 ±          78.928  ns/op
 * f      10000  avgt    5  ,   ,   ,161,307.430 ±       14417.439  ns/op
 * f     100000  avgt    5  ,   ,  4,319,503.788 ±       54048.416  ns/op
 * f    1000000  avgt    5  ,   ,143,473,057.032 ±     2967368.300  ns/op
 * f   10000000  avgt    5  ,  4,566,194,529.000 ±    35204294.208  ns/op
 * f  100000000  avgt    5  ,138,087,935,624.200 ±  3382798975.782  ns/op
 * i          1  avgt    5  ,   ,   ,   ,  7.882 ±           0.044  ns/op
 * i         10  avgt    5  ,   ,   ,   , 16.867 ±           0.099  ns/op
 * i        100  avgt    5  ,   ,   ,   ,411.462 ±          12.446  ns/op
 * i       1000  avgt    5  ,   ,   ,  5,974.059 ±          70.111  ns/op
 * i      10000  avgt    5  ,   ,   ,156,202.497 ±        3569.108  ns/op * 26
 * i     100000  avgt    5  ,   ,  4,089,266.144 ±       14196.204  ns/op * 26
 * i    1000000  avgt    5  ,   ,135,634,676.079 ±     1655240.462  ns/op * 33
 * i   10000000  avgt    5  ,  4,311,929,611.733 ±    42360887.350  ns/op * 31
 * i  100000000  avgt    5  ,140,610,753,027.200 ±  3784984704.328  ns/op * 32
 * i  536870919  avgt    1 1,477,207,603,865.000                    ns/op * 10
 *
 * recursive only:
 *    (digits)  Mode  Cnt          Score         Error  Units
 * f         1  avgt    5         20.337 ±       0.525  ns/op
 * f        10  avgt    5         30.219 ±       0.280  ns/op
 * f       100  avgt    5        445.816 ±       3.467  ns/op
 * f      1000  avgt    5       6191.403 ±     102.763  ns/op
 * f     10000  avgt    5     215233.169 ±    1158.594  ns/op
 * f    100000  avgt    5    7344824.551 ±   69596.871  ns/op
 * f   1000000  avgt    5  244801185.174 ± 1882321.877  ns/op
 *    (digits)  Mode  Cnt          Score         Error  Units
 * i         1  avgt    5          8.360 ±       0.742  ns/op
 * i        10  avgt    5         17.455 ±       0.268  ns/op
 * i       100  avgt    5        425.590 ±       5.101  ns/op
 * i      1000  avgt    5       6112.526 ±      99.375  ns/op
 * i     10000  avgt    5     213332.860 ±    4687.010  ns/op
 * i    100000  avgt    5    7494682.949 ±   72581.379  ns/op
 *
 * recursive+linear, recursionThreshold=64
 *    (digits)  Mode  Cnt     Score   Error  Units
 * i       100  avgt    2   304.980          ns/op
 * i       200  avgt    2   678.962          ns/op
 * i       300  avgt    2  1171.179          ns/op
 * i       500  avgt    2  1803.450          ns/op
 * i       700  avgt    2  3789.537          ns/op
 * i      1000  avgt    2  5033.718          ns/op
 * recursive+linear, recursionThreshold=128
 *    (digits)  Mode  Cnt          Score   Error  Units
 * i       100  avgt    2        202.121          ns/op
 * i       200  avgt    2        560.883          ns/op
 * i       300  avgt    2        870.945          ns/op
 * i       500  avgt    2       1553.730          ns/op
 * i       700  avgt    2       3144.256          ns/op
 * i      1000  avgt    2       4519.256          ns/op
 * i     10000  avgt    2     190314.904          ns/op
 * i    100000  avgt    2    7463072.826          ns/op
 * i   1000000  avgt    2  257700714.397          ns/op
 * recursive+linear, recursionThreshold=256
 *    (digits)  Mode  Cnt          Score   Error  Units
 * i       100  avgt    2        201.404          ns/op
 * i       200  avgt    2        460.926          ns/op
 * i       300  avgt    2        771.103          ns/op
 * i       500  avgt    2       1561.619          ns/op
 * i       700  avgt    2       2876.851          ns/op
 * i      1000  avgt    2       4496.300          ns/op
 * i     10000  avgt    2     185169.243          ns/op
 * i    100000  avgt    2    7286562.820          ns/op
 * i   1000000  avgt    2  254888361.325          ns/op
 *    (digits)  Mode  Cnt     Score   Error  Units
 * i       100  avgt    2   434.519          ns/op
 * i       200  avgt    2   985.514          ns/op
 * i       300  avgt    2  1236.896          ns/op
 * i       500  avgt    2  2276.103          ns/op
 * i       700  avgt    2  5287.722          ns/op
 * i      1000  avgt    2  6117.631          ns/op
 *
 * linear only:
 *    (digits)  Mode  Cnt     Score   Error  Units
 * i         1  avgt    2     8.866          ns/op
 * i        10  avgt    2    19.557          ns/op
 * i       100  avgt    2   200.758          ns/op
 * i       200  avgt    2   451.315          ns/op
 * i       300  avgt    2   835.332          ns/op
 * i       500  avgt    2  2138.143          ns/op
 * i       700  avgt    2  3760.165          ns/op
 * i      1000  avgt    2  7467.876          ns/op
 *
 * parse only:
 *    (digits)  Mode  Cnt      Score   Error  Units
 * f         1  avgt    2      8.453          ns/op
 * f        10  avgt    2     12.745          ns/op
 * f       100  avgt    2     19.145          ns/op
 * f      1000  avgt    2     75.527          ns/op
 * f     10000  avgt    2    647.700          ns/op
 * f    100000  avgt    2   6558.060          ns/op
 * f   1000000  avgt    2  79316.681          ns/op
 * i         1  avgt    2      5.273          ns/op
 * i        10  avgt    2      9.236          ns/op
 * i       100  avgt    2     16.014          ns/op
 * i      1000  avgt    2     69.027          ns/op
 * i     10000  avgt    2    680.891          ns/op
 * i    100000  avgt    2   5871.168          ns/op
 * i   1000000  avgt    2  70169.331          ns/op
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview", "-Xmx24g"

        //       ,"-XX:+UnlockDiagnosticVMOptions", "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/JavaBigDecimalParser.*"

})
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class LargeJavaBigDecimalFromByteArrayScalabilityJmh {


    @Param({
            //  "1",
            //  "10",
            "20",
            "30",
            "40",
            "50",
            "60",
            "70",
            "80",
            "90",
            //  "100",
            //  "200",
            //  "300",
            //  "500",
            //  "700",
            // "1000",
            //  "10000",
            //  "100000",
            //  "1000000",
            //  "10000000",
            //   "100000000",
            //    "536870919"
    })
    public int digits;
    private byte[] integerPart;
    private byte[] fractionalPart;

    @Setup(Level.Trial)
    public void setUp() {
        String str = "9806543217".repeat((digits + 9) / 10).substring(0, digits);
        integerPart = str.getBytes(StandardCharsets.ISO_8859_1);
        fractionalPart = ("0." + str).getBytes(StandardCharsets.ISO_8859_1);
    }

    @Benchmark
    public BigDecimal i() {
        return new JavaBigDecimalFromByteArray().parseBigDecimalString(integerPart, 0, integerPart.length);
    }
    // @Benchmark
    // public BigDecimal f() {
    //     return new LargeJavaBigDecimalFromByteArray().parseFloatingPointLiteral(fractionalPart,0,fractionalPart.length);
    // }
}





