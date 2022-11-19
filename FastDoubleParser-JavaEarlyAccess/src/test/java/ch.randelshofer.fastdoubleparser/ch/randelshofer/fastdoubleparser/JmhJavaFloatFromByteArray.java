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
 * # JMH version: 1.35
 * # VM version: JDK 19, OpenJDK 64-Bit Server VM, 19+36-2238
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * tryToReadEightDigits
 *              (str)  Mode  Cnt    Score   Error  Units
 * m                0  avgt    2    4.997          ns/op
 * m              365  avgt    2   12.126          ns/op
 * m             10.1  avgt    2   14.257          ns/op
 * m        3.1415927  avgt    2   21.029          ns/op
 * m    1.6162552E-35  avgt    2   28.545          ns/op
 * m  0x1.57bd4ep-116  avgt    2  370.203          ns/op
 *
 * tryToReadFourDigits
 * Benchmark    (str)  Mode  Cnt    Score   Error  Units
 * m                0  avgt    2    4.951          ns/op
 * m              365  avgt    2   12.300          ns/op
 * m             10.1  avgt    2   14.440          ns/op
 * m        3.1415927  avgt    2   20.244          ns/op
 * m    1.6162552E-35  avgt    2   26.573          ns/op
 * m  0x1.57bd4ep-116  avgt    2  363.786          ns/op
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
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhJavaFloatFromByteArray {


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





