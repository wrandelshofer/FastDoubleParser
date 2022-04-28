/*
 * @(#)DoubleParserJmhBenchmark.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 16, OpenJDK 64-Bit Server VM, 16+36-2231
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark    (str)  Mode  Cnt    Score   Error  Units
 * m                0  avgt    2    6.570          ns/op
 * m              365  avgt    2   14.525          ns/op
 * m             10.1  avgt    2   16.646          ns/op
 * m        3.1415927  avgt    2   24.022          ns/op
 * m    1.6162552E-35  avgt    2   28.465          ns/op
 * m  0x1.57bd4ep-116  avgt    2  336.391          ns/op
 *
 * // with 7 digits at once!
 * Benchmark                                  (str)  Mode  Cnt   Score   Error  Units
 * FastFloatParserFromByteArrayJmh.m      3.1415927  avgt    2  22.704          ns/op
 * FastFloatParserFromByteArrayJmh.m  1.6162552E-35  avgt    2  28.679          ns/op
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {"-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        //       ,"-XX:+UnlockDiagnosticVMOptions", "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/FastDoubleParser.*"

})
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class FastFloatParserFromByteArrayJmh {


    @Param({
            //  "0",
            //  "365",
            //  "10.1",
            "3.1415927",
            "1.6162552E-35",
            //   "0x1.57bd4ep-116"
    })
    public String str;
    private byte[] byteArray;

    @Setup
    public void prepare() {
        byteArray = str.getBytes(StandardCharsets.ISO_8859_1);
    }

    @Benchmark
    public double m() {
        return FastFloatParser.parseFloat(byteArray);
    }
}




