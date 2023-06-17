/*
 * @(#)JmhJavaFloatFromByteArray.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 20.0.1, OpenJDK 64-Bit Server VM, 20.0.1+9-29
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * FastDoubleSwar.isDigits() with return '0' <= c && c <= '9';
 * Benchmark                              (str)  Mode  Cnt    Score   Error  Units
 * JmhJavaFloatFromByteArray.m                0  avgt    4    4.613 ± 0.399  ns/op
 * JmhJavaFloatFromByteArray.m              365  avgt    4    8.795 ± 0.330  ns/op
 * JmhJavaFloatFromByteArray.m             10.1  avgt    4   12.456 ± 0.290  ns/op
 * JmhJavaFloatFromByteArray.m        3.1415927  avgt    4   16.972 ± 2.539  ns/op
 * JmhJavaFloatFromByteArray.m    1.6162552E-35  avgt    4   22.124 ± 0.188  ns/op
 * JmhJavaFloatFromByteArray.m  0x1.57bd4ep-116  avgt    4  365.855 ± 6.610  ns/op
 *
 * FastDoubleSwar.isDigits() with return (char) (c - '0') < 10;
 * Benchmark                              (str)  Mode  Cnt    Score   Error  Units
 * JmhJavaFloatFromByteArray.m                0  avgt    4    4.684 ± 0.568  ns/op
 * JmhJavaFloatFromByteArray.m              365  avgt    4    9.394 ± 0.108  ns/op
 * JmhJavaFloatFromByteArray.m             10.1  avgt    4   12.212 ± 0.090  ns/op
 * JmhJavaFloatFromByteArray.m        3.1415927  avgt    4   16.951 ± 2.436  ns/op
 * JmhJavaFloatFromByteArray.m    1.6162552E-35  avgt    4   23.245 ± 0.235  ns/op
 * JmhJavaFloatFromByteArray.m  0x1.57bd4ep-116  avgt    4  363.210 ± 4.485  ns/op
 * </pre>
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 17, OpenJDK 64-Bit Server VM, 17+35-2724
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark    (str)  Mode  Cnt    Score   Error  Units
 * m                0  avgt    2    6.570          ns/op
 * m              365  avgt    2   14.525          ns/op
 * m             10.1  avgt    2   16.646          ns/op
 * m        3.1415927  avgt    2   24.022          ns/op
 * m    1.6162552E-35  avgt    2   28.465          ns/op
 * m  0x1.57bd4ep-116  avgt    2  336.391          ns/op
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {"-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector",
        "--enable-preview"
        //       ,"-XX:+UnlockDiagnosticVMOptions", "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/FastDoubleParser.*"

})
@Measurement(iterations = 4)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhJavaFloatFromByteArrayEmpirical {


    @Param({
            "0",
            "365",
            "10.1",
            "3.1415927",
            "1.6162552E-35",
            "0x1.57bd4ep-116"
    })
    public String str;
    private byte[] byteArray;

    @Setup
    public void prepare() {
        byteArray = str.getBytes(StandardCharsets.ISO_8859_1);
    }

    @Benchmark
    public double m() {
        return JavaFloatParser.parseFloat(byteArray);
    }
}





